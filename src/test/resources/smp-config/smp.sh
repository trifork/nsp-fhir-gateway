#!/bin/bash -ex

url=http://localhost/
username=smp
password=123456

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A9902:DK42424242 \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<ServiceGroup
xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
<ParticipantIdentifier scheme="iso6523-actorid-upis">9902:DK42424242</ParticipantIdentifier>
<ServiceMetadataReferenceCollection/>
</ServiceGroup>'

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A9902:DK42424242/services/tc1%3A%3Atc1leg1 \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<ServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
   <ServiceInformation>
      <ParticipantIdentifier scheme="iso6523-actorid-upis">9902:DK42424242</ParticipantIdentifier>
      <DocumentIdentifier scheme="tc1">tc1leg1</DocumentIdentifier>
      <ProcessList>
         <Process>
            <ProcessIdentifier scheme="tc1">fod-emergence</ProcessIdentifier>
            <ServiceEndpointList>
               <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                  <EndpointURI>https://ap-gateway.nsptest.medcom.dk/domibus/services/msh</EndpointURI>
                  <Certificate>MIIGFjCCBP6gAwIBAgIEXem2jDANBgkqhkiG9w0BAQsFADBAMQswCQYDVQQGEwJE SzESMBAGA1UECgwJVFJVU1QyNDA4MR0wGwYDVQQDDBRUUlVTVDI0MDggT0NFUyBD QSBJVjAeFw0yMDEyMDcwOTMyMjdaFw0yMzEyMDcwOTI2MDFaMIGJMQswCQYDVQQG EwJESzEfMB0GA1UECgwWTWVkQ29tIC8vIENWUjoyNjkxOTk5MTFZMCAGA1UEBRMZ Q1ZSOjI2OTE5OTkxLUZJRDo5NDgyMjc3NTA1BgNVBAMMLm1lZGNvbS1lZGVsaXZl cnktZ2F0ZXdheSAoZnVua3Rpb25zY2VydGlmaWthdCkwggEiMA0GCSqGSIb3DQEB AQUAA4IBDwAwggEKAoIBAQDx3jo9QRe4kmcjdeEmjv5Yn+ONY8LeJNH4IWTs9t4H QseT9Sywdz31mxFMPjQ+5e8aEQFb1VFKXYT4edxe+NhrwcIUSrGBFRNWI1jOmkO6 BGsbrwcmuIBgRcAT4j2CRkchZNvG1AKlg9R+yvNfXXDvqQqQBL7C5r3X/nMM/uHz WpLp9nb5CjHitbQG0dByd46ncwIpEuPY9innZMcPzhMiG8LExh+QQ1n0dp7AEXnb JGXLnQ5aaua2tMbZTf139GQ8CC6lrPufp071OE70L1kGNvdFawiuNNG2ZuXjvMXQ M1LyPAIi6eVA7cXOXYiZPeVwlz3agHOLixhBrkXw4f17AgMBAAGjggLMMIICyDAO BgNVHQ8BAf8EBAMCA7gwgYkGCCsGAQUFBwEBBH0wezA1BggrBgEFBQcwAYYpaHR0 cDovL29jc3AuaWNhMDQudHJ1c3QyNDA4LmNvbS9yZXNwb25kZXIwQgYIKwYBBQUH MAKGNmh0dHA6Ly9mLmFpYS5pY2EwNC50cnVzdDI0MDguY29tL29jZXMtaXNzdWlu ZzA0LWNhLmNlcjCCAUMGA1UdIASCATowggE2MIIBMgYKKoFQgSkBAQEEAzCCASIw LwYIKwYBBQUHAgEWI2h0dHA6Ly93d3cudHJ1c3QyNDA4LmNvbS9yZXBvc2l0b3J5 MIHuBggrBgEFBQcCAjCB4TAQFglUUlVTVDI0MDgwAwIBARqBzEZvciBhbnZlbmRl bHNlIGFmIGNlcnRpZmlrYXRldCBn5mxkZXIgT0NFUyB2aWxr5XIsIENQUyBvZyBP Q0VTIENQLCBkZXIga2FuIGhlbnRlcyBmcmEgd3d3LnRydXN0MjQwOC5jb20vcmVw b3NpdG9yeS4gQmVt5nJrLCBhdCBUUlVTVDI0MDggZWZ0ZXIgdmlsa+VyZW5lIGhh ciBldCBiZWdy5m5zZXQgYW5zdmFyIGlmdC4gcHJvZmVzc2lvbmVsbGUgcGFydGVy LjCBlwYDVR0fBIGPMIGMMC6gLKAqhihodHRwOi8vY3JsLmljYTA0LnRydXN0MjQw OC5jb20vaWNhMDQuY3JsMFqgWKBWpFQwUjELMAkGA1UEBhMCREsxEjAQBgNVBAoM CVRSVVNUMjQwODEdMBsGA1UEAwwUVFJVU1QyNDA4IE9DRVMgQ0EgSVYxEDAOBgNV BAMMB0NSTDE3NDMwHwYDVR0jBBgwFoAUXLt1YhYymao2oLia+2+nDF/wCtUwHQYD VR0OBBYEFI6Z5MBc/e0TeKqgK99Xpa4ClXcyMAkGA1UdEwQCMAAwDQYJKoZIhvcN AQELBQADggEBAJ5Q+K8bFwwS18np8Kl8a7G+6Z08mAvIZFKwFRAuIeNjBZpiS0Om vX4Tw54AEjOZ6FGInOxj4Yp6QLMghAhy2Ov2NpEMIer8NPxDtjKxxM0xyUIl1jB+ k3FG2XIM8NXn/X1Ze580HvB+nUXqBKYihPyUEnZbIQL4xcnbfrBSHv3nD0Y7RshU LXM1hFcSFYK4T2dKlXuHqXzfUpAk+s+mnZUlSkkCxKVEPugEWLuXYCmWOOcjqY2L yPuIdzj+oMaNQUGWEveK0LNJAmVEn1fWKr4lqMdsFjDcGDFTwrKe1HNkLi5Di2Cs ysfxzHhqfs5R/gJ7XmZ7J3m8OEErXxxH9cw=</Certificate>
                  <ServiceDescription/>
                  <TechnicalContactUrl/>
               </Endpoint>
            </ServiceEndpointList>
         </Process>
         <Process>
            <ProcessIdentifier scheme="tc1">fod-distribution</ProcessIdentifier>
            <ServiceEndpointList>
               <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                  <EndpointURI>http://212.98.96.26:8081/domibus/services/msh</EndpointURI>
                  <Certificate>MIIGKjCCBRKgAwIBAgIEWnqRnDANBgkqhkiG9w0BAQsFADBBMQswCQYDVQQGEwJESzESMBAGA1UE CgwJVFJVU1QyNDA4MR4wHAYDVQQDDBVUUlVTVDI0MDggT0NFUyBDQSBJSUkwHhcNMTkxMTIyMDkx NzQxWhcNMjIxMTIyMDkxNjM4WjCBmzELMAkGA1UEBhMCREsxMTAvBgNVBAoMKERpZ2l0YWxpc2Vy aW5nc3N0eXJlbHNlbiAvLyBDVlI6MzQwNTExNzgxWTAgBgNVBAUTGUNWUjozNDA1MTE3OC1GSUQ6 OTMyMDAwNTkwNQYDVQQDDC5lRGVsaXZlcnkgQWNjZXNzIFBvaW50czIgKGZ1bmt0aW9uc2NlcnRp ZmlrYXQpMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3M9tQfFkZSiyMmmOmDm2FSv1 yg7w0YYfW02SDQVTy1HP0Z1JtUD1K8nY3pt4LfqOt2LXLzM8W558yJ9cpAxxw45u4eRUxWS7nbzP ETR3o2T4slUxRI8hQlD/I7jjFj86C0oOxexdBwmXQoGF3UrP4N9zJYfY2mkXClkPn3dWXkm4wOQb llWyFctJPV7lWSA5XcJxVasDz1QR460Bs7nrP7BzmHJq5qDtijnhtoBvikUcxfNVM7357fxnpONU utB1fViq1HOSzAFhS7BkMHvwCtVPFAbJMndTisyeBlN4qsjfvX3GPfaKXYnTzKY8rfiFCWpXs3PA 2x7OQbdEQzuchwIDAQABo4ICzTCCAskwDgYDVR0PAQH/BAQDAgO4MIGJBggrBgEFBQcBAQR9MHsw NQYIKwYBBQUHMAGGKWh0dHA6Ly9vY3NwLmljYTAzLnRydXN0MjQwOC5jb20vcmVzcG9uZGVyMEIG CCsGAQUFBzAChjZodHRwOi8vZi5haWEuaWNhMDMudHJ1c3QyNDA4LmNvbS9vY2VzLWlzc3Vpbmcw My1jYS5jZXIwggFDBgNVHSAEggE6MIIBNjCCATIGCiqBUIEpAQEBBAIwggEiMC8GCCsGAQUFBwIB FiNodHRwOi8vd3d3LnRydXN0MjQwOC5jb20vcmVwb3NpdG9yeTCB7gYIKwYBBQUHAgIwgeEwEBYJ VFJVU1QyNDA4MAMCAQEagcxGb3IgYW52ZW5kZWxzZSBhZiBjZXJ0aWZpa2F0ZXQgZ+ZsZGVyIE9D RVMgdmlsa+VyLCBDUFMgb2cgT0NFUyBDUCwgZGVyIGthbiBoZW50ZXMgZnJhIHd3dy50cnVzdDI0 MDguY29tL3JlcG9zaXRvcnkuIEJlbeZyaywgYXQgVFJVU1QyNDA4IGVmdGVyIHZpbGvlcmVuZSBo YXIgZXQgYmVncuZuc2V0IGFuc3ZhciBpZnQuIHByb2Zlc3Npb25lbGxlIHBhcnRlci4wgZgGA1Ud HwSBkDCBjTAuoCygKoYoaHR0cDovL2NybC5pY2EwMy50cnVzdDI0MDguY29tL2ljYTAzLmNybDBb oFmgV6RVMFMxCzAJBgNVBAYTAkRLMRIwEAYDVQQKDAlUUlVTVDI0MDgxHjAcBgNVBAMMFVRSVVNU MjQwOCBPQ0VTIENBIElJSTEQMA4GA1UEAwwHQ1JMNTk1OTAfBgNVHSMEGDAWgBTGWDFPli1S+/YL ePfK3B7Y2ryjhDAdBgNVHQ4EFgQU29mX4aSN+HCQFnbDT7fESmvQKWowCQYDVR0TBAIwADANBgkq hkiG9w0BAQsFAAOCAQEAjijvTZmmwH4fZDZ5pB/+UIf+HTucPKDWRHCipGUXb4JrreWX5+OjEfAY VK3GLnW/jE3C9jfhcMvagD9em7hig3XkRfpDsRlfYzh35i44QoV/eHZuonMMg//lt4n1q8+ynWiQ zgUyMjTZjmBimZB+Vk/5CCe0b2IiUcl/j9YmoNh3dtFAoUXxl/I479QEwmBDi9Uzkyc9McafBU0S 9r5xRQ2So1lqzBOJBV93DHbdeC2BYhJWfShFD0dswBVWqtf2qZDa2ewwcbRyNCnL83zP5ofe6nPz 3Iyi3G7x41di6XPlrcxMBGlpOOpSdlB7JhcAGz5wOyCgaWoRsviWcDW20Q==</Certificate>
                  <ServiceDescription/>
                  <TechnicalContactUrl/>
               </Endpoint>
            </ServiceEndpointList>
         </Process>
      </ProcessList>
   </ServiceInformation>
</ServiceMetadata>'

