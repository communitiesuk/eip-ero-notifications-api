import { AxiosError, AxiosResponse } from "axios";
import fs from "fs";
import { NotifyClient } from "notifications-node-client";
import { TemplateData } from "notifications-node-client/types/client/notification";
import { toFileContent } from "./helpers";

const apiKey = process.env.NOTIFY_TEMPLATE_BACKUP_API_KEY;
if (!apiKey) {
  throw new Error("NOTIFY_TEMPLATE_BACKUP_API_KEY environment variable is required");
}

const templateIdsToBackup = [
    "6d0490ee-e004-402e-808f-5791e8336ddb",
    "71251c81-c771-412f-bf44-21db8e6ff092",
]

const notifyClient = new NotifyClient(apiKey);

type TemplateFile = {
    name: string;
    content: string;
}

const templatesToDelete: string[] = fs.readdirSync("./templates");
const newOrUpdatedTemplates: TemplateFile[] = [];

console.log(`Found ${templatesToDelete.length} existing template files`);

for (const id of templateIdsToBackup) {
    const template = await notifyClient.getTemplateById(id)
        .then((response: AxiosResponse<TemplateData>) => response.data)
        .catch((e: AxiosError) => {
            // what do we want to happen where a template is not found?
            // For example if a template has been mistakenly deleted in error.
            // Alarm in slack?
            console.warn(`⚠️  Template ${id} not found in Notify (${e.response?.status ?? e.message}), skipping`);
            return null;
        });
    if (!template) { continue; }
    const fileContent = toFileContent(template);
    const filename = `${template.name} - ${template.type} - ${id}.txt`

    if (templatesToDelete.includes(filename)) {
        templatesToDelete.splice(templatesToDelete.indexOf(filename), 1);
        const existingTemplateFileContent = fs.readFileSync(`./templates/${filename}`, 'utf8');
        if (existingTemplateFileContent === fileContent) { continue; }
    }

    newOrUpdatedTemplates.push({ name: filename, content: fileContent });
}

const hasChanges = templatesToDelete.length > 0 || newOrUpdatedTemplates.length > 0;

if (!hasChanges) {
    console.log("No changes detected, nothing to do.");
} else {
    for (const filename of templatesToDelete) {
        console.log(`Deleting: ${filename}`);
        fs.unlinkSync(`./templates/${filename}`);
    }

    for (const { name, content } of newOrUpdatedTemplates) {
        console.log(`Writing: ${name}`);
        fs.writeFileSync(`./templates/${name}`, content, "utf8");
    }

    console.log(`\nDeleted ${templatesToDelete.length}, written ${newOrUpdatedTemplates.length}.`);
}

console.log("\nbackup-templates: done");
