import { AxiosError, AxiosResponse } from "axios";
import fs from "fs";
import { NotifyClient } from "notifications-node-client";
import { TemplateData } from "notifications-node-client/types/client/notification";

type TemplateFile = {
    name: string;
    content: string;
}

const toBackupFilename = (templateName: string, templateId: string): string => {
    const safeName = templateName.replace(/[/\\:*?"<>|]/g, '');
    return `${safeName} - ${templateId}.txt`;
};

const toFileContent = (template: TemplateData): string => {
    const sections: string[] = [`Communication type:\n${template.type}`];

    if (template.subject) {
        sections.push(`Subject/Heading:\n${template.subject}`);
    }
    if (template.letter_contact_block) {
        sections.push(`Letter contact block:\n${template.letter_contact_block}`);
    }
    if (template.postage) {
        sections.push(`Postage:\n${template.postage}`);
    }

    sections.push(toNotifyPasteableText(template.body));

    return sections.join("\n---\n");
};

/**
 * Converts a Notify API template body into plain text suitable for
 * pasting back into the Notify UI.
 *
 * The API returns the body using Notify's own markup syntax
 * (# headings, ((placeholders)), ^ insets, [links](url), --- rules, * bullets)
 * with \r\n line endings. This is already the format the UI accepts,
 * so we just normalise line endings and trim trailing whitespace per line.
 */
const toNotifyPasteableText = (body: string): string =>
    body
        .replace(/\r\n/g, "\n")
        .replace(/[ \t]+$/gm, "")
        .replace(/\n+$/, "\n");

const checkForDuplicateIds = (templateIds: Record<string, string>): void => {
    const entries: [string, string][] = Object.entries(templateIds);
    const seen = new Map<string, string>();
    for (const [name, id] of entries) {
        const existing: string | undefined = seen.get(id);
        if (existing) {
            throw new Error(`Duplicate ID received (${id}). Templates ${existing} and ${name} both have the same ID`);
        }
        seen.set(id, name);
    }
}

export const backupTemplates = async (
    notifyClient: NotifyClient,
    templateIds: Record<string, string>,
): Promise<void> => {
    checkForDuplicateIds(templateIds);
    const backupDir = "./backups";

    if (!fs.existsSync(backupDir)) {
        fs.mkdirSync(backupDir, { recursive: true });
    }
    const templatesToDelete: Set<string> = new Set(fs.readdirSync(backupDir, { encoding: 'utf8' }));
    const newOrUpdatedTemplates: TemplateFile[] = [];

    console.log(`Found ${templatesToDelete.size} existing template files`);

    for (const [name, id] of Object.entries(templateIds)) {
        console.log(`Checking template: ${name} (${id})`);
        const response: AxiosResponse<TemplateData> | null = await notifyClient.getTemplateById(id)
            .catch((e: unknown) => {
                const axiosError = e as AxiosError;
                console.warn(`⚠️  Template ${id} not found in Notify (${axiosError.response?.status ?? axiosError.message}), skipping`);
                console.warn(`Raw error response data:`, JSON.stringify(axiosError.response?.data, null, 2));
                return null;
            });
        if (!response) { continue; }
        console.log(`--- Raw response status: ${response.status}`);
        console.log(`--- Raw response headers:`, JSON.stringify(response.headers, null, 2));
        console.log(`--- Raw response data (full):`, JSON.stringify(response.data, null, 2));
        const template: TemplateData = response.data;
        console.log(`--- Parsed TemplateData keys:`, Object.keys(template));
        if (!template) { continue; }
        const fileContent: string = toFileContent(template);
        const filename: string = toBackupFilename(template.name, id);

        if (templatesToDelete.has(filename)) {
            templatesToDelete.delete(filename);
            const existingTemplateFileContent: string = fs.readFileSync(`${backupDir}/${filename}`, 'utf8');
            if (existingTemplateFileContent === fileContent) { continue; }
        }

        newOrUpdatedTemplates.push({ name: filename, content: fileContent });
    }

    const hasChanges: boolean = templatesToDelete.size > 0 || newOrUpdatedTemplates.length > 0;

    if (hasChanges) {
        for (const filename of templatesToDelete) {
            console.log(`Deleting: ${filename}`);
            fs.unlinkSync(`${backupDir}/${filename}`);
        }

        for (const { name, content } of newOrUpdatedTemplates) {
            console.log(`Writing: ${name}`);
            fs.writeFileSync(`${backupDir}/${name}`, content, "utf8");
        }

        console.log(`\nDeleted ${templatesToDelete.size}, written ${newOrUpdatedTemplates.length}.`);
    }

    console.log("\nbackup-templates: done");
};
