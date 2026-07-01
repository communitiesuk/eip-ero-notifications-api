import { jest, describe, it, expect, beforeEach } from "@jest/globals";
import type { TemplateData } from "notifications-node-client/types/client/notification";
import type { NotifyClient } from "notifications-node-client";

const mockExistsSync = jest.fn<typeof import("fs").existsSync>();
const mockMkdirSync = jest.fn<typeof import("fs").mkdirSync>();
const mockReaddirSync = jest.fn<() => string[]>();
const mockReadFileSync = jest.fn<() => string>();
const mockWriteFileSync = jest.fn<typeof import("fs").writeFileSync>();
const mockUnlinkSync = jest.fn<typeof import("fs").unlinkSync>();

jest.unstable_mockModule("fs", () => ({
    default: {
        existsSync: mockExistsSync,
        mkdirSync: mockMkdirSync,
        readdirSync: mockReaddirSync,
        readFileSync: mockReadFileSync,
        writeFileSync: mockWriteFileSync,
        unlinkSync: mockUnlinkSync,
    },
}));

const { backupTemplates } = await import("./backup-templates.functions");

const mockGetAllTemplates = jest.fn<() => Promise<{ data: { templates: TemplateData[] } }>>();
const mockNotifyClient = {
    getAllTemplates: mockGetAllTemplates,
} as unknown as NotifyClient;

const templateData = (name: string, id: string, body: string, type = "email"): TemplateData => ({
    id,
    name,
    type,
    body,
    version: 1,
    created_at: "2024-01-01T00:00:00Z",
    updated_at: "2024-01-01T00:00:00Z",
    created_by: "test@example.com",
    uri: `https://api.notifications.service.gov.uk/v2/template/${id}`,
}) as unknown as TemplateData;

beforeEach(() => {
    jest.clearAllMocks();
    mockExistsSync.mockReturnValue(true);
    mockReaddirSync.mockReturnValue([]);
});

describe("backupTemplates", () => {
    describe("template unchanged", () => {
        it("does not write when template content has not changed", async () => {
            const id = "abc-123";
            const templateName = "Test Email - EN";
            const filename = `${templateName} - ${id}.txt`;
            const body = "Hello ((name))\n";
            const expectedContent = `Communication type:\nemail\n---\n${body}`;

            mockReaddirSync.mockReturnValue([filename]);
            mockReadFileSync.mockReturnValue(expectedContent);
            mockGetAllTemplates.mockResolvedValue({
                data: { templates: [templateData(templateName, id, body)] },
            });

            await backupTemplates(mockNotifyClient, { test_email_en: id });

            expect(mockWriteFileSync).not.toHaveBeenCalled();
            expect(mockUnlinkSync).not.toHaveBeenCalled();
        });
    });

    describe("template updated", () => {
        it("writes new content when template has changed", async () => {
            const id = "abc-123";
            const templateName = "Test Email - EN";
            const filename = `${templateName} - ${id}.txt`;
            const oldBody = "Old content\n";
            const newBody = "New content\n";
            const oldContent = `Communication type:\nemail\n---\n${oldBody}`;

            mockReaddirSync.mockReturnValue([filename]);
            mockReadFileSync.mockReturnValue(oldContent);
            mockGetAllTemplates.mockResolvedValue({
                data: { templates: [templateData(templateName, id, newBody)] },
            });

            await backupTemplates(mockNotifyClient, { test_email_en: id });

            expect(mockWriteFileSync).toHaveBeenCalledWith(
                `./backups/${filename}`,
                expect.stringContaining("New content"),
                "utf8",
            );
        });
    });

    describe("template deleted from notify", () => {
        it("removes the file when template no longer exists in notify", async () => {
            const id = "abc-123";
            const filename = `Test Email - EN - ${id}.txt`;
            const consoleSpy = jest.spyOn(console, "warn");

            mockReaddirSync.mockReturnValue([filename]);
            mockGetAllTemplates.mockResolvedValue({
                data: { templates: [] },
            });

            await backupTemplates(mockNotifyClient, { test_email_en: id });

            expect(mockUnlinkSync).toHaveBeenCalledWith(`./backups/${filename}`);
            expect(consoleSpy).toHaveBeenCalledWith(`⚠️  Template test_email_en ${id} not found in Notify, skipping`);
        });
    });

    describe("new template", () => {
        it("creates a file for a template not previously backed up", async () => {
            const id = "def-456";
            const templateName = "New Letter - CY";
            const body = "Dear ((name))\n";

            mockReaddirSync.mockReturnValue([]);
            mockGetAllTemplates.mockResolvedValue({
                data: { templates: [templateData(templateName, id, body, "letter")] },
            });

            await backupTemplates(mockNotifyClient, { new_letter_cy: id });

            expect(mockWriteFileSync).toHaveBeenCalledWith(
                `./backups/${templateName} - ${id}.txt`,
                expect.stringContaining("Dear ((name))"),
                "utf8",
            );
        });
    });

    describe("backup directory does not exist", () => {
        it("creates the directory before proceeding", async () => {
            mockExistsSync.mockReturnValue(false);
            mockReaddirSync.mockReturnValue([]);
            mockGetAllTemplates.mockResolvedValue({
                data: { templates: [] },
            });

            await backupTemplates(mockNotifyClient, {});

            expect(mockMkdirSync).toHaveBeenCalledWith("./backups", { recursive: true });
        });
    });

    describe("duplicate template IDs", () => {
        it("throws an error when the same ID appears more than once", async () => {
            const duplicateId = "abc-123";
            const templateIds = {
                template_one: duplicateId,
                template_two: duplicateId,
            };

            await expect(backupTemplates(mockNotifyClient, templateIds))
                .rejects.toThrow(`Duplicate ID received (${duplicateId}). Templates template_one and template_two both have the same ID`);
        });
    });
});
