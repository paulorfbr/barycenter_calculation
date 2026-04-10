# WireMock Test Resources

This directory contains WireMock stub mappings for external service mocking.

## Directory Structure

```
wiremock/
├── mappings/          # Request-response mappings
│   ├── company-api/   # External company API mocks
│   ├── geolocation/   # Geographic services mocks  
│   └── auth/          # Authentication service mocks
└── __files/           # Response body files
    ├── json/          # JSON response templates
    └── xml/           # XML response templates
```

## Usage

1. Create mapping files in `mappings/` directory
2. Reference response files from `__files/` directory
3. Use `BaseWireMockTest` for automatic setup

## Example Mapping

```json
{
  "request": {
    "method": "GET",
    "urlPathEqualTo": "/api/companies/123"
  },
  "response": {
    "status": 200,
    "headers": {
      "Content-Type": "application/json"
    },
    "bodyFileName": "json/company-response.json"
  }
}
```