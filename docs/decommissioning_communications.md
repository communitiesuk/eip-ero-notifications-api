# Decommissioning Communications

If communication types are replaced with another (for example, replacing `REJECTED_SIGNATURE` and `REQUESTED_SIGNATURE` with `SIGNATURE_RESUBMISSION`), after release, the old communication types can be decommissioned.
Decommissioning involves the removal of:
- Preview functionality
- Sending functionality
across Infra, Notifications API, Applications API and the Portal, whilst preserving the communication type for the purposes of statistics and sent communications.

## Enums that must be left

Since applications can in theory be retained indefinitely, and sent communications can be requested, some enum values for the old communications must remain:
- `database/entity/NotificationType`: this enum is used during sent communications request to retrieve communications from the database
- `dto/NotificationType`: this enum is used during sent communications to process the request
- `model/TemplateType`: this enum (defined in the API spec) is used during sent communications requests to return the communications in the response
