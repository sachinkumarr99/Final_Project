# Legal Metrology Online Verification

A Spring Boot backend for managing legal-metrology instrument verification from registration through certificate generation. Business users register instruments and submit verification applications. Administrators configure verification standards and assign work. Legal Metrology Officers (LMOs) schedule inspections, submit readings, and produce an approval or rejection result. Documents are stored through Cloudinary and metadata is persisted in MongoDB.

This README is written for frontend development and describes the HTTP contract exposed by the current source code.

## What The Project Does

The application supports this workflow:

1. A business user creates an account and signs in.
2. The user registers a measuring instrument.
3. The user uploads instrument documents.
4. The user submits a verification or re-verification application.
5. An administrator creates verification standards and assigns the application to an LMO or GATC user.
6. The assigned LMO views the application, schedules an inspection, and records test readings.
7. The backend compares actual readings with configured standards and marks the result `APPROVED` or `REJECTED`.
8. An administrator generates a certificate for an approved application.
9. Anyone with the certificate verification token can verify the certificate through a public endpoint.

## Technology

- Java 21
- Spring Boot 4.1.1
- Spring MVC and Bean Validation
- Spring Security with stateless JWT authentication
- MongoDB with Spring Data MongoDB
- Cloudinary for uploaded files
- Thymeleaf and OpenHTMLToPDF for certificate generation
- ZXing for QR-code generation
- Redis and Kafka dependencies are present in Maven, although the main controllers shown here do not expose Redis/Kafka endpoints
- Maven Wrapper (`mvnw` and `mvnw.cmd`)

## Project Structure

```text
src/main/java/com/LegalMeterology/Online_Verification/
├── Configurations/       MongoDB, Cloudinary, and security configuration
├── Controllers/          REST controllers
├── Dto/                  Validated request objects
├── Entities/             MongoDB domain documents
├── Enums/                Roles, statuses, instrument types, and categories
├── Exceptions/           Domain exceptions and global error handling
├── Filters/              JWT request filter
├── Mappers/              Entity/DTO conversion
├── Repositories/         MongoDB repositories
├── Responses/            API envelopes and upload responses
└── Services/             Business logic

src/main/resources/
├── application.yaml      Base application configuration
├── application-dev.yaml  Development secrets and Cloudinary placeholders
└── templates/
    └── Certificate.html  Certificate rendering template
```

## Running Locally

### Prerequisites

- JDK 21
- MongoDB running locally on port `27017`
- A Cloudinary account for document upload and certificate assets
- Maven is optional because the repository includes Maven Wrapper

The default MongoDB database is:

```text
mongodb://localhost:27017/legal_metrology
```

### Environment variables

Set these variables before starting the application:

```text
CLOUDINARY_NAME=<cloudinary cloud name>
CLOUDINARY_API_KEY=<cloudinary api key>
CLOUDINARY_API_SECRET=<cloudinary api secret>
```

The development configuration also contains the JWT secret and a 24-hour token expiration setting. For production, move the JWT secret out of source-controlled configuration and provide it through an environment-specific secret store.

### Start the backend

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Unix-like shells:

```bash
./mvnw spring-boot:run
```

The API is available at:

```text
http://localhost:8080
```

### Build and test

```bash
./mvnw clean test
```

On Windows use `mvnw.cmd clean test`.

## Authentication And Authorization

Login returns a JWT as a plain JSON string. Send it on every protected request:

```http
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

Security rules configured in `SecurityConfig`:

| Route | Access |
|---|---|
| `/public/**` | Public, no token required |
| `/admin/**` | `ADMIN` authority |
| `/lmo/**` | `LMO` authority |
| All other routes | Authenticated user |

The configured role hierarchy is:

```text
ADMIN > LMO
GATC_ADMIN > LMO
LMO > GATC_OFFICER
GATC_OFFICER > BUSINESS_USER
```

Roles used by the application are `ADMIN`, `BUSINESS_USER`, `LMO`, `GATC_ADMIN`, and `GATC_OFFICER`.

## Response Formats

There are three response styles. Frontend code should inspect the HTTP status and then handle the response shape for the specific endpoint.

### Wrapped API response

Used by user instrument and application endpoints:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {}
}
```

### Direct entity/list response

Admin, LMO, document, and certificate endpoints commonly return an entity, a list, or a service-specific response directly without `success`, `message`, and `data`.

### Plain string response

Registration returns a plain string, and sign-in returns a plain JWT string:

```json
"User Registerd Successfully"
```

The spelling of `Registerd` is currently part of the implementation.

## Error Response

The global exception handler generally returns this shape:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Request could not be processed",
  "apiPath": "/user/register_instrument",
  "timestamp": "2026-09-09T10:00:00"
}
```

Common statuses:

| Status | Meaning |
|---|---|
| `400` | Validation failure, malformed JSON, invalid enum/value, or business-state error |
| `401` | Missing/invalid JWT or invalid credentials |
| `403` | Authenticated user lacks the required authority |
| `404` | Requested user/resource was not found |
| `409` | Duplicate resource or verification conflict |
| `500` | File upload failure in the upload controller |

Validation errors are produced by `@Valid`. A frontend should display `message` and keep the original `apiPath` for debugging.

# API Reference

All URLs below are relative to `http://localhost:8080`.

## Public Endpoints

### Register a business user

```http
POST /public/register
```

Request body:

```json
{
  "email": "user@example.com",
  "mobile": "9876543210",
  "password": "Abcd@1234",
  "firstName": "Sachin",
  "lastName": "Kumar",
  "address": "12 Market Road",
  "city": "Delhi",
  "state": "Delhi",
  "isOrganization": false
}
```

The endpoint validates the request and always registers the account as `BUSINESS_USER`. Success: `201 Created`.

```json
"User Registerd Successfully"
```

### Sign in

```http
POST /public/signIn
```

Request body:

```json
{
  "email": "user@example.com",
  "password": "Abcd@1234"
}
```

Success: `200 OK`. The body is a plain JWT string:

```json
"eyJhbGciOiJIUzI1NiJ9..."
```

### Verify a certificate publicly

```http
GET /public/certificates/verify/{verificationToken}
```

No authentication is required. The endpoint returns the certificate verification result directly. The exact fields are the serialized certificate response produced by `CertificateService`.

## Business User Endpoints

All routes in this section require a JWT. They operate on the currently authenticated business user.

### Register an instrument

```http
POST /user/register_instrument
```

Request body:

```json
{
  "instrumentType": "WEIGHING_MACHINE",
  "manufacturer": "Mettler Toledo",
  "model": "MT-500",
  "serialNumber": "SER-1001",
  "capacity": 500,
  "accuracyClass": "CLASS_II",
  "unit": "KILOGRAM",
  "installationLocation": "Delhi Warehouse"
}
```

Success: `201 Created`.

```json
{
  "success": true,
  "message": "Instrument has been registerd successfully",
  "data": {
    "instrumentNumber": "INS-2026-000001",
    "instrumentType": "WEIGHING_MACHINE",
    "status": "UNVERIFIED"
  }
}
```

`instrumentNumber` is generated by the backend. The complete instrument entity is returned by the list endpoint.

### Upload instrument documents

```http
POST /documents/instrument/upload_with_categories
```

This is a protected `multipart/form-data` request, not JSON. Include one value for each field:

| Field | Type | Description |
|---|---|---|
| `instrumentNumber` | text | Existing instrument number |
| `categories` | repeated text | One `DocumentCategory` for each file |
| `files` | repeated file | Files in the same order as `categories` |

Example form fields:

```text
instrumentNumber=INS-2026-000001
categories=INSTRUMENT_PHOTO
categories=PURCHASE_INVOICE
categories=MANUFACTURER_DOCUMENT
files=<photo.jpg>
files=<invoice.pdf>
files=<manufacturer.pdf>
```

The number of files must equal the number of categories. Each category may be uploaded only once for an instrument. The instrument must already exist.

Success: `200 OK`.

```json
[
  {
    "category": "INSTRUMENT_PHOTO",
    "originalFileName": "photo.jpg",
    "cloudinaryPublicId": "INSTRUMENT_PHOTO_1724758000_a1b2c3d4",
    "fileUrl": "https://res.cloudinary.com/example/image/upload/photo.jpg",
    "fileFormat": "image/jpeg",
    "fileSizeInByte": 123456
  }
]
```

### Submit a verification application

```http
POST /user/apply_verification_instrument
```

Request body:

```json
{
  "instrumentNumber": "INS-2026-000001",
  "applicationType": "VERIFICATION"
}
```

`applicationType` is `VERIFICATION` or `RE_VERIFICATION`. The instrument must exist, the user must own it, and the required documents must be available: `INSTRUMENT_PHOTO`, `PURCHASE_INVOICE`, and `MANUFACTURER_DOCUMENT`.

Success: `200 OK`.

```json
{
  "success": true,
  "message": "Verification Application Successfully Submitted",
  "data": {
    "applicationNumber": "APP-2026-000001",
    "instrumentNumber": "INS-2026-000001",
    "status": "SUBMITTED"
  }
}
```

### List the user's instruments

```http
GET /user/instruments
```

Success: `200 OK`.

```json
{
  "message": "Instrument getted Successfully",
  "success": true,
  "data": [
    {
      "id": "64f5a0e2d1b8e2e9d0a1b2c3",
      "instrumentNumber": "INS-2026-000001",
      "instrumentType": "WEIGHING_MACHINE",
      "manufacturer": "Mettler Toledo",
      "model": "MT-500",
      "serialNumber": "SER-1001",
      "capacity": 500,
      "accuracyClass": "CLASS_II",
      "unit": "KILOGRAM",
      "installationLocation": "Delhi Warehouse",
      "status": "UNVERIFIED",
      "ownerId": "user_123",
      "createdAt": "2026-08-29T09:40:00"
    }
  ]
}
```

### List the user's applications

```http
GET /user/applications
```

Success: `200 OK`.

```json
{
  "message": "Application getted Successfully",
  "success": true,
  "data": [
    {
      "id": "64f5a1e2d1b8e2e9d0a1b2c3",
      "applicationNumber": "APP-2026-000001",
      "userId": "user_123",
      "instrumentNumber": "INS-2026-000001",
      "applicationType": "VERIFICATION",
      "status": "SUBMITTED",
      "createdAt": "2026-08-29T09:42:00"
    }
  ]
}
```

## Administrator Endpoints

The security configuration restricts every `/admin/**` route to `ADMIN`. These endpoints return direct entities, lists, or plain strings unless stated otherwise.

### Register an LMO

```http
POST /admin/register_LMO
```

Request body uses the same user fields as public registration. The backend registers the new user as `LMO`.

Success: `201 Created`.

```json
"LMO Registerd Successfully"
```

### Register a GATC organization

```http
POST /admin/register_Organization
```

Request body uses the same user fields. The backend registers the new user as `GATC_ADMIN`.

Success: `201 Created`.

```json
"LMO Registerd Successfully"
```

The response text currently says `LMO` even for an organization.

### Create a verification standard

```http
POST /admin/verification-standards
```

Request body:

```json
{
  "instrumentType": "WEIGHING_MACHINE",
  "testPoints": [
    {
      "testValue": 10,
      "expectedValue": 10,
      "permissibleError": 0.5
    },
    {
      "testValue": 20,
      "expectedValue": 20,
      "permissibleError": 0.75
    }
  ]
}
```

Success: `201 Created`. Returns the created verification-standard document directly:

```json
{
  "id": "64f5a8d0d1b8e2e9d0a1b2c9",
  "instrumentType": "WEIGHING_MACHINE",
  "testPoints": [
    {
      "testValue": 10,
      "expectedValue": 10,
      "permissibleError": 0.5
    }
  ]
}
```

### Assign an application

```http
POST /admin/assigned_application
```

Request body:

```json
{
  "applicationNumber": "APP-2026-000001",
  "assignedToType": "LMO",
  "assignedToId": "64f5a0d0d1b8e2e9d0a1b2c4",
  "remarks": "Assigned for field verification"
}
```

`assignedToType` may be `LMO`, `GATC_ORGANIZATION`, or `GATC_OFFICER`. The target user's role must match the selected type. Duplicate assignment for the same application and administrator is rejected.

Success: `200 OK` and an assignment entity, for example:

```json
{
  "id": "64f5a3d1d1b8e2e9d0a1b2c6",
  "applicationNumber": "APP-2026-000001",
  "assignedToType": "LMO",
  "assignedToId": "64f5a0d0d1b8e2e9d0a1b2c4",
  "assignedById": "admin_123",
  "status": "ASSIGNED",
  "remarks": "Assigned for field verification",
  "assignedAt": "2026-08-29T10:30:00"
}
```

### List all applications

```http
GET /admin/applications
```

Success: `200 OK` and an array of verification-application entities.

### List users by role

```http
GET /admin/all_user?role=LMO
```

`role` is required and accepts `ADMIN`, `BUSINESS_USER`, `LMO`, `GATC_ADMIN`, or `GATC_OFFICER`. Success: `200 OK` and an array of user entities. Password hashes should never be rendered or stored by the frontend; the current entity serialization should be reviewed before exposing this endpoint outside a trusted admin UI.

### Generate a certificate

```http
GET /admin/generateCertificate/{applicationNumber}
```

The service generates the certificate for the application, using the HTML template, PDF conversion, and QR-code support configured in the project. Success: `200 OK` with the generated certificate service response. The exact response fields are defined by `CertificateService` and its certificate response/entity classes.

### List application evidence

```http
GET /admin/evidences/{applicationNumber}
```

Success: `200 OK` and an array of stored verification-evidence entities for the application.

## LMO Endpoints

Every `/lmo/**` route is restricted by the main security configuration to `LMO` authority.

### List assigned applications

```http
GET /lmo/applications
```

Success: `200 OK` and the assignments/applications assigned to the current LMO.

### Get one application

```http
GET /lmo/applications/{applicationNumber}
```

Success: `200 OK` and the selected application. The LMO service checks the application/assignment relationship.

### Schedule verification

```http
POST /lmo/applications/{applicationNumber}/schedule
```

Request body:

```json
{
  "scheduledDate": "2026-08-30"
}
```

The date is required and cannot be in the past. Duplicate scheduling is rejected. Success: `200 OK` and the created schedule record. The application status changes to `SCHEDULED`.

### Submit verification readings

```http
POST /lmo/applications/{applicationNumber}/verify
```

Request body:

```json
{
  "readings": [
    {
      "testValue": 10,
      "actualValue": 10.2
    },
    {
      "testValue": 20,
      "actualValue": 19.8
    }
  ]
}
```

`readings` must be non-empty. For each test point, the service looks up the configured expected value and permissible error, then calculates:

```text
error = actualValue - expectedValue
passed = abs(error) <= permissibleError
```

If every reading passes, the result is `APPROVED`; otherwise it is `REJECTED`. The instrument status is updated to `VERIFIED` or `REJECTED`. Success: `200 OK` and the stored verification result, including calculated readings.

Representative response:

```json
{
  "id": "64f5b0d1d1b8e2e9d0a1b2cb",
  "applicationNumber": "APP-2026-000001",
  "instrumentNumber": "INS-2026-000001",
  "lmoId": "lmo_123",
  "readings": [
    {
      "testValue": 10,
      "expectedValue": 10,
      "actualValue": 10.2,
      "error": 0.2,
      "permissibleError": 0.5,
      "passed": true
    }
  ],
  "result": "APPROVED",
  "verifiedAt": "2026-08-29T11:00:00"
}
```

## Document Review Endpoints

These routes are declared under `/documents`. They require authentication. The source uses method-level `@PreAuthorize` checks for LMO/officer/admin review operations in addition to the route-level security rules.

### Get instrument documents for an application

```http
GET /documents/verificationApplication/{applicationNumber}
```

Returns the documents associated with the application. The service response is returned directly.

### Get uploaded verification evidence

```http
GET /documents/evidence/{applicationNumber}
```

Returns verification evidence uploaded for the application.

### Verify an instrument document

The controller declares this route as:

```http
GET /documents/instrument/document/verify/{docuemntId}
```

The handler method parameter is named `documentId`, while the route placeholder is named `docuemntId`. This should be corrected in the backend before relying on the endpoint; otherwise Spring may fail to bind the path variable. Intended response: the verified document/service result.

### Verify verification evidence

```http
GET /documents/evidence/verify/{documentId}
```

The intended authority is `GATC_ADMIN` or `ADMIN`, and the service returns the verified evidence result directly.

## Enums

Use these exact uppercase values in request bodies, query parameters, and form fields.

| Enum | Values |
|---|---|
| `Role` | `ADMIN`, `BUSINESS_USER`, `LMO`, `GATC_ADMIN`, `GATC_OFFICER` |
| `ApplicationStatus` | `SUBMITTED`, `APPROVED`, `REJECTED`, `SCHEDULED`, `ASSIGNED` |
| `ApplicationType` | `VERIFICATION`, `RE_VERIFICATION` |
| `AssignmentType` | `LMO`, `GATC_ORGANIZATION`, `GATC_OFFICER` |
| `InstrumentType` | `WEIGHING_MACHINE`, `PETROL`, `LENGTH_MEASURING`, `VOLUME_CAPACITY`, `FUEL_DISPENSER` |
| `Unit` | `METER`, `KILOGRAM`, `GRAM`, `LITRE` |
| `AccuracyClass` | `CLASS_I`, `CLASS_II`, `CLASS_III`, `CLASS_IIII` |
| `DocumentCategory` | `INSTRUMENT_PHOTO`, `PURCHASE_INVOICE`, `MANUFACTURER_DOCUMENT`, `PREVIOUS_VERIFICATION_CERTIFICATE` |
| Instrument status | `UNVERIFIED`, `UNDER_VERIFICATION`, `VERIFIED`, `EXPIRED`, `REJECTED` |

## Suggested Frontend Flow

1. Call `POST /public/register` for a business account.
2. Call `POST /public/signIn` and store the returned token using the frontend's security policy.
3. Attach `Authorization: Bearer <token>` to protected requests.
4. Call `POST /user/register_instrument` and retain the generated `instrumentNumber`.
5. Upload the three required document categories with `multipart/form-data`.
6. Call `POST /user/apply_verification_instrument`.
7. Poll or refresh `GET /user/applications` to display status.
8. For administrator screens, load `GET /admin/applications`, load users with `GET /admin/all_user?role=LMO`, and assign work.
9. For LMO screens, load assigned applications, schedule the visit, and submit readings.
10. For approved applications, let an administrator generate a certificate and expose the verification token/URL to the user.

## Frontend Integration Notes

- Do not assume every successful response has a `data` property.
- Treat the sign-in response as a string token, not `{ token: "..." }`.
- Use `multipart/form-data` for document upload and do not manually set a multipart boundary when using `FormData` in browser clients.
- Preserve enum spelling and capitalization exactly.
- Do not send a password back to the UI if a user entity contains a password hash in an admin response.
- Handle `401` by clearing the session and redirecting to sign-in; handle `403` as an authorization failure; handle `409` as a domain conflict that can usually be shown inline.
- The backend currently allows all CORS origins (`*`) for development. Restrict this to the frontend origin before production deployment.
- The controller response messages include existing spelling mistakes. Frontend code should use status/data rather than matching those messages as stable identifiers.

## Data And Persistence Overview

MongoDB stores users, instruments, verification applications, assignments, uploaded-document metadata, verification standards, verification results, certificates, and counters used for generated numbers. Cloudinary stores uploaded binary documents and returns public URLs and IDs. The certificate service combines stored verification data with `Certificate.html`, creates a PDF, and uses QR-code support for public certificate verification.

## Current Implementation Notes

- `FRONTEND_API_SPEC.md` contains an earlier frontend contract; this README includes the additional certificate, evidence, and document-review routes present in the controllers.
- The project includes Redis and Kafka starters, but no public REST behavior in this README depends on them.
- Several controller methods use `ResponseEntity<?>`, so exact response schemas can vary by service implementation.
- The document controller has a path-variable spelling mismatch on the instrument-document verification route.
- The security configuration uses role hierarchy and method-level authority checks. Verify those checks with integration tests when adding new frontend roles.
