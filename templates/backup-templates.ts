import { AxiosError, AxiosResponse } from "axios";
import fs from "fs";
import { NotifyClient } from "notifications-node-client";
import { TemplateData } from "notifications-node-client/types/client/notification";
import { toFileContent } from "./helpers";

const BACKUP_DIR = "./templates";

const apiKey = process.env.NOTIFY_TEMPLATE_BACKUP_API_KEY;
if (!apiKey) {
    throw new Error("NOTIFY_TEMPLATE_BACKUP_API_KEY environment variable is required");
}

const templateIdsJson = process.env.NOTIFY_TEMPLATE_IDS;
if (!templateIdsJson) {
    throw new Error("NOTIFY_TEMPLATE_IDS environment variable is required");
}

const templateIds: Record<string, string> = JSON.parse(templateIdsJson);

const notifyClient = new NotifyClient(apiKey);

type TemplateFile = {
    name: string;
    content: string;
}

const templatesToDelete: string[] = fs.readdirSync(BACKUP_DIR);
const newOrUpdatedTemplates: TemplateFile[] = [];

console.log(`Found ${templatesToDelete.length} existing template files`);

for (const [name, id] of Object.entries(templateIds)) {
    console.log(`Checking template: ${name} (${id})`);
    const template = await notifyClient.getTemplateById(id)
        .then((response: AxiosResponse<TemplateData>) => response.data)
        .catch((e: AxiosError) => {
            console.warn(`⚠️  Template ${id} not found in Notify (${e.response?.status ?? e.message}), skipping`);
            return null;
        });
    if (!template) { continue; }
    const fileContent = toFileContent(template);
    const filename = `${template.name} - ${template.type} - ${id}.txt`

    if (templatesToDelete.includes(filename)) {
        templatesToDelete.splice(templatesToDelete.indexOf(filename), 1);
        const existingTemplateFileContent = fs.readFileSync(`${BACKUP_DIR}/${filename}`, 'utf8');
        if (existingTemplateFileContent === fileContent) { continue; }
    }

    newOrUpdatedTemplates.push({ name: filename, content: fileContent });
}

const hasChanges = templatesToDelete.length > 0 || newOrUpdatedTemplates.length > 0;

if (hasChanges) {
    for (const filename of templatesToDelete) {
        console.log(`Deleting: ${filename}`);
        fs.unlinkSync(`${BACKUP_DIR}/${filename}`);
    }

    for (const { name, content } of newOrUpdatedTemplates) {
        console.log(`Writing: ${name}`);
        fs.writeFileSync(`${BACKUP_DIR}/${name}`, content, "utf8");
    }

    console.log(`\nDeleted ${templatesToDelete.length}, written ${newOrUpdatedTemplates.length}.`);
}

console.log("\nbackup-templates: done");
