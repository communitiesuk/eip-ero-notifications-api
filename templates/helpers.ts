import { TemplateData } from "notifications-node-client/types/client/notification";

export const toFileContent = (template: TemplateData): string => {
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
export const toNotifyPasteableText = (body: string): string =>
    body
        .replace(/\r\n/g, "\n")
        .replace(/[ \t]+$/gm, "")
        .replace(/\n+$/, "\n");
