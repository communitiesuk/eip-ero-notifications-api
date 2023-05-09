# GOV.UK Notify Templates

Notification API provides a proxy to the [UK Government Notify service](https://www.notifications.service.gov.uk/), which handles the sending of various
communications by email, text or letter. The full documentation around the can be found [here](https://www.notifications.service.gov.uk/using-notify/api-documentation).

Once you have been given access you will be able to see the templates that are already defined, edit them and create new ones.
The template ids are then referenced by Notifications API via environment variables mapped to properties in `application.yml`.

## Template Preview - Notification API Response

Notifications REST API has an endpoint described in Open API as:

    POST /templates/{templateType}/preview
    
    Valid template types are: application-received, application-approved, application-rejected, photo-resubmission
    
    Request includes an optional personalisation map containing template placeholder values, for example:
    {
        "personalisation": {
            "name": "Fred",
            "surname": "Blogs"
        }
    }
    
    Response includestext*string
    example: Hi Fred Blogs,Text preview of the template with personalisation datasubjectstring
    example: Fred Blogs photo resubmissionPreview of the subject with personalisation data, if the template supports subject fieldhtmlstring
    example: <p>Hi Fred Blogs,</p>Html preview of the template with personalisation data, if the template supports html format


Notify Service templates support following formatting options:

    Formatting
    To put a title in your template, use a hash:

    # This is a title
    To make bullet points, use asterisks:

    * point 1
    * point 2
    * point 3
      To add inset text, use a caret:

    ^ You must tell us if your circumstances change
    To add a horizontal line, use three dashes:

    First paragraph

    ---

    Second paragraph


Following section is the above details inserted as Mark up:

Formatting
To put a title in your template, use a hash:

# This is a title

To make bullet points, use asterisks:

* point 1
* point 2
* point 3 To add inset text, use a caret:

^ You must tell us if your circumstances change
To add a horizontal line, use three dashes:

First paragraph

---

Second paragraph

## Sent Message - Notification API Available Data

Notify send an email API returns a SendEmailResponse that is stored in the DynamoDB along with details of the request including personalisation contents.

    {
        "id": "5d2e3fb6-edea-4d71-a78d-70e8e08d6bc8",
        "channel": "EMAIL",
        "gssCode": "E99999999",
        "notifyDetails": {
            "body": "Dear Neil\r\n\r\n# Application Reference JIRA-EIP1-1942-Test-For_Joshua\r\n\r\nIn reference to application reference JIRA-EIP1-1942-Test-For_Joshua, we need you to send us a new or updated photo.",
            "fromEmail": "electoralregistrationofficerportal@notifications.service.gov.uk",
            "notificationId": "3d755bb0-dfb2-4c0c-be90-a15fa1a2c8a3",
            "reference": "5d2e3fb6-edea-4d71-a78d-70e8e08d6bc8",
            "subject": "New Photo request for Application Reference JIRA-EIP1-1942-Test-For_Joshua",
            "templateId": "f1571006-c3a0-4c97-884a-189f5b103f85",
            "templateUri": "https://api.notifications.service.gov.uk/services/137e13d7-6acd-4449-815e-de0eb0c083ba/templates/f1571006-c3a0-4c97-884a-189f5b103f85",
            "templateVersion": 1
        },
        "personalisation": {
            "applicationReference": "JIRA-EIP1-1942-Test-For_Joshua",
            "firstName": "Neil"
        },
        "requestor": "neil.massey@valtech.com",
        "sentAt": "2022-10-18T08:31:37.425500319",
        "sourceReference": "JIRA-EIP1-1942-TestJoshua",
        "sourceType": "VOTER_CARD",
        "toEmail": "neil.massey@valtech.com",
        "type": "PHOTO_RESUBMISSION"
    }

It should be noted that **ALL** the details returned from the Notify service is included in the above item, the Java client response object has properties:

    public class SendEmailResponse {
    private final UUID notificationId;
    private final String reference;
    private final UUID templateId;
    private final int templateVersion;
    private final String templateUri;
    private final String body;
    private final String subject;
    private final String fromEmail;
    ...
    @Override
    public String toString() {
    return "SendEmailResponse{" +
            "notificationId=" + notificationId +
            ", reference=" + reference +
            ", templateId=" + templateId +
            ", templateVersion=" + templateVersion +
            ", templateUri='" + templateUri + '\'' +
            ", body='" + body + '\'' +
            ", subject='" + subject + '\'' +
            ", fromEmail=" + fromEmail +
            '}';
        }
    }

All request placeholders are saved as is the templateId used for sending so would be possible to use the generateTemplatePreview for a sent notification.

Alternatively whilst sending an email the generate Template Preview can be called and its HTML stored alongside the sent details.

## Missing Placeholder

Requesting a template preview or attempting to send a notification without a placeholder causes the Notify service to return a 400 Bad Request with response body:

    {
        "timestamp": "2022-12-22T11:20:51.681Z",
        "status": 400,
        "error": "Bad Request",
        "message": "Status code: 400 {\"errors\":[{\"error\":\"BadRequestError\",\"message\":\"Missing personalisation: missingPlaceholder\"}],\"status_code\":400}\n"
    }


## Extra Placeholder

Requesting a template preview or sending a notification with a placeholder that is not used in the template does not cause any validation error and the template/send operation completes successfully.  This could be useful if we ever need to edit and existing template with new placeholders, we can send the placeholder in advance and then update the template.


## Placeholder Markup

UK Gov Notify service templates support a limited amount of mark up as detailed above.  Including markup with the placeholder values is supported and changes the rendered HTML, for example `# Creates a title  and * makes placeholder a bullet`.  Generally formatting would be expected to be part of the template and not the placeholders - after some trials the formatting was not reliable in placeholders.  Probably safest to avoid using titles and bullets in a free form text placeholder.  The undocumented ** syntax for bold, e.g.  &ast;&ast;Fred&ast;&ast; did not work - translated template included the **  as text.

Example Request

    {
        "channel": "email",
        "language": "en",
        "personalisation": {
            "applicationReference": "A3JSZC4CRH",
            "firstName": "**Fred**",
            "photoRequestFreeText": "Please provide a clear image",
            "uploadPhotoLink": "helloworld",
            "eroContactDetails": {
                "localAuthorityName": "City of Sunderland",
                "website": "not good",
                "phone": "01234 567890",
                "email": "fred.blogs@some-domain.co.uk",
                "address": {
                    "street": "This is a title Charles Lane Street",
                    "property": "* Some Property",
                    "locality": "* Some locality",
                    "town": "* London",
                    "area": "* Charles Area",
                    "postcode": "# PE3 6SB"
                }
            }
        }
    }




## Placeholder HTML

Sent request with HTML formatting tags for paragraph, bold, strong, emphasis and list item:

    {
        "channel": "email",
        "language": "en",
        "personalisation": {
            "applicationReference": "A3JSZC4CRH",
            "firstName": "<b>Fred</b>",
            "photoRequestFreeText": "<p>Please provide a clear image</p>",
            "uploadPhotoLink": "helloworld",
            "eroContactDetails": {
                "localAuthorityName": "City of Sunderland",
                "website": "not good",
                "phone": "01234 567890",
                "email": "fred.blogs@some-domain.co.uk",
                "address": {
                    "street": "This is a title Charles Lane Street",
                    "property": "<li>Some Property</li>",
                    "locality": "<em>Some locality</em>",
                    "town": "<strong>London</strong>",
                    "area": "<em>Charles Area</em>",
                    "postcode": "PE3 6SB"
                }
            }
        }
    }

The placeholder value is escaped in the rendered HTML and therefore appears as it does in the placeholder, for example "<em>Charles Area</em>" is in the HTML as `&amp;lt;em&amp;gt;Charles Area&amp;lt;/em&amp;gt;`




## Blank Placeholders

As documented in section 3 all placeholders MUST be included in a request so values that are not known are sent as blank strings (not null values).

    Template includes ERO Address as:
    ((eroAddressLine1))
    ((eroAddressLine2))
    ((eroAddressLine3))
    ((eroAddressLine4))
    ((eroAddressPostcode))
    
    Request sent to Notify service includes blank values like:
        "address": {
            "property": "Some Property",
            "street": "Charles Lane Street in bullets",
            "town": "",
            "area": "",
            "postcode": "PE3 6SB"
        }




The HTML returned will render with an empty line between address and postcode as below:

![template_img.png](template_img.png)

## Template Versioning

Template versioning is not currently supported.
In order to allow for control over template changes and specifically when breaking changes are introduced all templates
should now include a version number in the name e.g.
    
    Rejected Documents Email - EN - v1.0

When a template requires a breaking change (e.g. addition of a new placeholder) then a new copy of the template should
be created and the version number incremented. This will then allow for controlled rollout by allowing the new template id
to be specified after the required service changes have already been deployed.
