import { NotifyClient } from "notifications-node-client";
import { backupTemplates } from "./backup-templates.functions";

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

await backupTemplates(notifyClient, templateIds);
