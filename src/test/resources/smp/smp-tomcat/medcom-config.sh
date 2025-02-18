#!/bin/bash -ex

url=http://localhost:8080/
username=smp
password=123456

echo 'Waiting for SMP to become available...'
bash -c 'while [[ "$(curl -w %{http_code} http://localhost:8080)" != "302" ]]; do sleep 5; echo "Retrying $(date)"; done'
echo 'SMP finished startup'

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A0088:dds \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<ServiceGroup
xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
<ParticipantIdentifier scheme="iso6523-actorid-upis">0088:dds</ParticipantIdentifier>
<ServiceMetadataReferenceCollection/>
</ServiceGroup>'

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A0088:ap1 \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<ServiceGroup
xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
<ParticipantIdentifier scheme="iso6523-actorid-upis">0088:ap1</ParticipantIdentifier>
<ServiceMetadataReferenceCollection/>
</ServiceGroup>'

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A0088:ap2 \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<ServiceGroup
xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
<ParticipantIdentifier scheme="iso6523-actorid-upis">0088:ap2</ParticipantIdentifier>
<ServiceMetadataReferenceCollection/>
</ServiceGroup>'

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A0088:dds/services/urn:dk:healthcare:medcom:messaging:oioxml%3A%3Aurn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aoioxml%3Aschema%3Axsd%3Aclinicalemail%23urn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aoioxml%3Aschema%3Axsd%3Aclinicalemail%3Axdis91%3Axd9134l \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<ServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
   <ServiceInformation>
      <ParticipantIdentifier scheme="iso6523-actorid-upis">0088:dds</ParticipantIdentifier>
      <DocumentIdentifier scheme="urn:dk:healthcare:medcom:messaging:oioxml">urn:dk:healthcare:medcom:messaging:oioxml:schema:xsd:clinicalemail#urn:dk:healthcare:medcom:messaging:oioxml:schema:xsd:clinicalemail:xdis91:xd9134l</DocumentIdentifier>
      <ProcessList>
         <Process>
            <ProcessIdentifier scheme="dk-messaging-procid">urn:www.digst.dk:profile:sdn-emergence</ProcessIdentifier>
            <ServiceEndpointList>
               <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                  <EndpointURI>http://domibus-gateway:8080/domibus/services/msh</EndpointURI>
                  <Certificate>MIIFXDCCA0SgAwIBAgIJAL9WEqdLOEA3MA0GCSqGSIb3DQEBCwUAMEsxCzAJBgNV
BAYTAkRLMRAwDgYDVQQKDAdUcmlmb3JrMRAwDgYDVQQLDAdUcmlmb3JrMRgwFgYD
VQQDDA9kb21pYnVzLWdhdGV3YXkwHhcNMjIwMTE3MDk1MjU1WhcNMjcwNzEwMDk1
MjU1WjBLMQswCQYDVQQGEwJESzEQMA4GA1UECgwHVHJpZm9yazEQMA4GA1UECwwH
VHJpZm9yazEYMBYGA1UEAwwPZG9taWJ1cy1nYXRld2F5MIICIjANBgkqhkiG9w0B
AQEFAAOCAg8AMIICCgKCAgEAuVE0158JnBTjs81dxH3CblQwk5OkXdKu2cuSQlMo
QQBspe2Tkiy3VyoHfjk+5QJvPaTSqbcY7Uh1gfSWzNqi3apYHkvbZDngfLAysLMV
fkZQxU72i9N5YH1QLXsxYPu2hV0PtIA1OJEl2MHjJNm3Ka5KNGnq8TgaSir+EVtu
tj0Xa7s4EZYRnw+5cwQVsEXyYLgTOWUb9rcLiK+7Q1xne/SJ6SHox2wqDBKT96Ap
xNtaO2pMDrMYxw+euUFSq9G+QiJoj+yu4E1wrdpSVGo+sFeLRIihpCU59QtbjR/I
MP2qVgit/ETcUnJ/neufjiiexyP1KlO2C3zlMEhcosBdpmA5xTnkjWTWhPWHVBPS
qOnEd8wI7jKMRixGUoUjiQZVahR4yY76g/B1mKmIdvfLMa4yUWlcxfxr020cpAZp
JsyH3xDj8CReldw3Dq3O/NCqUB/RLnRhCmkiDMrCRYbnDiK620ybwclxm9aIvhGE
FJJ2xnhucSeuSB9jhY5wBzYT6sZq4I550SFe+neKiCN4IeuN00lDUccMjijSUKRH
weQg/QxtMj/DDIGFfmwBSJg81Sf9jd2br8STlwBK9o3DI0lmc6NyUK09tAoLEfDf
8zXhbHL6or7NvzKE3m6XYDkLQaIq8EPOHmHa9j2/MemR+Y4TZ40EleVtvFQeFRVK
AyUCAwEAAaNDMEEwDgYDVR0PAQH/BAQDAgOIMBMGA1UdJQQMMAoGCCsGAQUFBwMB
MBoGA1UdEQQTMBGCD3d3dy50cmlmb3JrLmNvbTANBgkqhkiG9w0BAQsFAAOCAgEA
B/jUcsTopr4Vyp2fb0EhLV3mEB9T3m9wJdLIXDtUwF+MbZ7TPyfWJbUymwmgl7eY
1PmQHp69MWKZwimkjRK1B2cs5pV8woGZf/9s8HGpclkvXops5QhZwQdv0NSTS3bl
iyVgvDNa+OMjp0dS7uBdev07qnVHqd4jrRKOtDpthrgLBGEil1u4lSWW8ATccSv6
Puh9SyG+OT5RZrqHFVQhGkn6PK6OiK3bqd29noHq0nK9qE147vnUoLoVQjPAyPgs
Ehm6eVqWVGPIc6609Djki5uIxzV4gicVz2q+aZQ3s/tKAN1UxbkmiMY+jY576L2Q
9dXElBQn3OqzRnr0YlrWRBInafMS8WGdcin6+jK3kYzgjD99MThC9iem431Pjzg5
Ox0vsAG1kNNF6Wmi0FiCejlEvW/0lpWb/foEXBs14E6fGF1jOFVEkqH82fIQt64c
6Egw241LFiNQqbuNDBKS9JKsAkktTQ+jzNLZAPw4qMcI3Q4NIU5zhkzAxHVKOx89
SJRFy66NXhIq+etfRC6B6kPfhpW82mRme8JXXwP00a4VtNrrMbYNC7l2TWpIC6J/
vDeHvoHhJftHkg5NtebN9vqOhNazncWLq7dfgZvaV425W4mZl1R42h9ktMa1Crhk
HBqzfhx8u0GCJwPAVo9AqndV2ahKLH3+WOfiwMGimzw=</Certificate>
                  <ServiceDescription/>
                  <TechnicalContactUrl/>
               </Endpoint>
            </ServiceEndpointList>
         </Process>
      </ProcessList>
   </ServiceInformation>
</ServiceMetadata>'

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A0088:dds/services/urn:dk:healthcare:medcom:messaging:ebxml%3A%3Aurn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aebxml%3Aschema%3Axsd%3Asbdhreceiptacknowledgement%23urn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aebxml%3Aschema%3Axsd%3Asbdhreceiptacknowledgement%3Aebbp-signals%3Aebbp-signals-2.0 \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<ServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
   <ServiceInformation>
      <ParticipantIdentifier scheme="iso6523-actorid-upis">0088:dds</ParticipantIdentifier>
      <DocumentIdentifier scheme="urn:dk:healthcare:medcom:messaging:ebxml">urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgement#urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgement:ebbp-signals:ebbp-signals-2.0</DocumentIdentifier>
      <ProcessList>
         <Process>
            <ProcessIdentifier scheme="dk-messaging-procid">urn:www.digst.dk:profile:sdn-emergence</ProcessIdentifier>
            <ServiceEndpointList>
               <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                  <EndpointURI>http://domibus-gateway:8080/domibus/services/msh</EndpointURI>
                  <Certificate>MIIFXDCCA0SgAwIBAgIJAL9WEqdLOEA3MA0GCSqGSIb3DQEBCwUAMEsxCzAJBgNV
BAYTAkRLMRAwDgYDVQQKDAdUcmlmb3JrMRAwDgYDVQQLDAdUcmlmb3JrMRgwFgYD
VQQDDA9kb21pYnVzLWdhdGV3YXkwHhcNMjIwMTE3MDk1MjU1WhcNMjcwNzEwMDk1
MjU1WjBLMQswCQYDVQQGEwJESzEQMA4GA1UECgwHVHJpZm9yazEQMA4GA1UECwwH
VHJpZm9yazEYMBYGA1UEAwwPZG9taWJ1cy1nYXRld2F5MIICIjANBgkqhkiG9w0B
AQEFAAOCAg8AMIICCgKCAgEAuVE0158JnBTjs81dxH3CblQwk5OkXdKu2cuSQlMo
QQBspe2Tkiy3VyoHfjk+5QJvPaTSqbcY7Uh1gfSWzNqi3apYHkvbZDngfLAysLMV
fkZQxU72i9N5YH1QLXsxYPu2hV0PtIA1OJEl2MHjJNm3Ka5KNGnq8TgaSir+EVtu
tj0Xa7s4EZYRnw+5cwQVsEXyYLgTOWUb9rcLiK+7Q1xne/SJ6SHox2wqDBKT96Ap
xNtaO2pMDrMYxw+euUFSq9G+QiJoj+yu4E1wrdpSVGo+sFeLRIihpCU59QtbjR/I
MP2qVgit/ETcUnJ/neufjiiexyP1KlO2C3zlMEhcosBdpmA5xTnkjWTWhPWHVBPS
qOnEd8wI7jKMRixGUoUjiQZVahR4yY76g/B1mKmIdvfLMa4yUWlcxfxr020cpAZp
JsyH3xDj8CReldw3Dq3O/NCqUB/RLnRhCmkiDMrCRYbnDiK620ybwclxm9aIvhGE
FJJ2xnhucSeuSB9jhY5wBzYT6sZq4I550SFe+neKiCN4IeuN00lDUccMjijSUKRH
weQg/QxtMj/DDIGFfmwBSJg81Sf9jd2br8STlwBK9o3DI0lmc6NyUK09tAoLEfDf
8zXhbHL6or7NvzKE3m6XYDkLQaIq8EPOHmHa9j2/MemR+Y4TZ40EleVtvFQeFRVK
AyUCAwEAAaNDMEEwDgYDVR0PAQH/BAQDAgOIMBMGA1UdJQQMMAoGCCsGAQUFBwMB
MBoGA1UdEQQTMBGCD3d3dy50cmlmb3JrLmNvbTANBgkqhkiG9w0BAQsFAAOCAgEA
B/jUcsTopr4Vyp2fb0EhLV3mEB9T3m9wJdLIXDtUwF+MbZ7TPyfWJbUymwmgl7eY
1PmQHp69MWKZwimkjRK1B2cs5pV8woGZf/9s8HGpclkvXops5QhZwQdv0NSTS3bl
iyVgvDNa+OMjp0dS7uBdev07qnVHqd4jrRKOtDpthrgLBGEil1u4lSWW8ATccSv6
Puh9SyG+OT5RZrqHFVQhGkn6PK6OiK3bqd29noHq0nK9qE147vnUoLoVQjPAyPgs
Ehm6eVqWVGPIc6609Djki5uIxzV4gicVz2q+aZQ3s/tKAN1UxbkmiMY+jY576L2Q
9dXElBQn3OqzRnr0YlrWRBInafMS8WGdcin6+jK3kYzgjD99MThC9iem431Pjzg5
Ox0vsAG1kNNF6Wmi0FiCejlEvW/0lpWb/foEXBs14E6fGF1jOFVEkqH82fIQt64c
6Egw241LFiNQqbuNDBKS9JKsAkktTQ+jzNLZAPw4qMcI3Q4NIU5zhkzAxHVKOx89
SJRFy66NXhIq+etfRC6B6kPfhpW82mRme8JXXwP00a4VtNrrMbYNC7l2TWpIC6J/
vDeHvoHhJftHkg5NtebN9vqOhNazncWLq7dfgZvaV425W4mZl1R42h9ktMa1Crhk
HBqzfhx8u0GCJwPAVo9AqndV2ahKLH3+WOfiwMGimzw=</Certificate>
                  <ServiceDescription/>
                  <TechnicalContactUrl/>
               </Endpoint>
            </ServiceEndpointList>
         </Process>
      </ProcessList>
   </ServiceInformation>
</ServiceMetadata>'

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A0088:dds/services/urn:dk:healthcare:medcom:messaging:ebxml%3A%3Aurn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aebxml%3Aschema%3Axsd%3Asbdhreceiptacknowledgementexception%23urn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aebxml%3Aschema%3Axsd%3Asbdhreceiptacknowledgementexception%3Aebbp-signals%3Aebbp-signals-2.0 \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<ServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
   <ServiceInformation>
      <ParticipantIdentifier scheme="iso6523-actorid-upis">0088:dds</ParticipantIdentifier>
      <DocumentIdentifier scheme="urn:dk:healthcare:medcom:messaging:ebxml">urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgementexception#urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgementexception:ebbp-signals:ebbp-signals-2.0</DocumentIdentifier>
      <ProcessList>
         <Process>
            <ProcessIdentifier scheme="dk-messaging-procid">urn:www.digst.dk:profile:sdn-emergence</ProcessIdentifier>
            <ServiceEndpointList>
               <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                  <EndpointURI>http://domibus-gateway:8080/domibus/services/msh</EndpointURI>
                  <Certificate>MIIFXDCCA0SgAwIBAgIJAL9WEqdLOEA3MA0GCSqGSIb3DQEBCwUAMEsxCzAJBgNV
BAYTAkRLMRAwDgYDVQQKDAdUcmlmb3JrMRAwDgYDVQQLDAdUcmlmb3JrMRgwFgYD
VQQDDA9kb21pYnVzLWdhdGV3YXkwHhcNMjIwMTE3MDk1MjU1WhcNMjcwNzEwMDk1
MjU1WjBLMQswCQYDVQQGEwJESzEQMA4GA1UECgwHVHJpZm9yazEQMA4GA1UECwwH
VHJpZm9yazEYMBYGA1UEAwwPZG9taWJ1cy1nYXRld2F5MIICIjANBgkqhkiG9w0B
AQEFAAOCAg8AMIICCgKCAgEAuVE0158JnBTjs81dxH3CblQwk5OkXdKu2cuSQlMo
QQBspe2Tkiy3VyoHfjk+5QJvPaTSqbcY7Uh1gfSWzNqi3apYHkvbZDngfLAysLMV
fkZQxU72i9N5YH1QLXsxYPu2hV0PtIA1OJEl2MHjJNm3Ka5KNGnq8TgaSir+EVtu
tj0Xa7s4EZYRnw+5cwQVsEXyYLgTOWUb9rcLiK+7Q1xne/SJ6SHox2wqDBKT96Ap
xNtaO2pMDrMYxw+euUFSq9G+QiJoj+yu4E1wrdpSVGo+sFeLRIihpCU59QtbjR/I
MP2qVgit/ETcUnJ/neufjiiexyP1KlO2C3zlMEhcosBdpmA5xTnkjWTWhPWHVBPS
qOnEd8wI7jKMRixGUoUjiQZVahR4yY76g/B1mKmIdvfLMa4yUWlcxfxr020cpAZp
JsyH3xDj8CReldw3Dq3O/NCqUB/RLnRhCmkiDMrCRYbnDiK620ybwclxm9aIvhGE
FJJ2xnhucSeuSB9jhY5wBzYT6sZq4I550SFe+neKiCN4IeuN00lDUccMjijSUKRH
weQg/QxtMj/DDIGFfmwBSJg81Sf9jd2br8STlwBK9o3DI0lmc6NyUK09tAoLEfDf
8zXhbHL6or7NvzKE3m6XYDkLQaIq8EPOHmHa9j2/MemR+Y4TZ40EleVtvFQeFRVK
AyUCAwEAAaNDMEEwDgYDVR0PAQH/BAQDAgOIMBMGA1UdJQQMMAoGCCsGAQUFBwMB
MBoGA1UdEQQTMBGCD3d3dy50cmlmb3JrLmNvbTANBgkqhkiG9w0BAQsFAAOCAgEA
B/jUcsTopr4Vyp2fb0EhLV3mEB9T3m9wJdLIXDtUwF+MbZ7TPyfWJbUymwmgl7eY
1PmQHp69MWKZwimkjRK1B2cs5pV8woGZf/9s8HGpclkvXops5QhZwQdv0NSTS3bl
iyVgvDNa+OMjp0dS7uBdev07qnVHqd4jrRKOtDpthrgLBGEil1u4lSWW8ATccSv6
Puh9SyG+OT5RZrqHFVQhGkn6PK6OiK3bqd29noHq0nK9qE147vnUoLoVQjPAyPgs
Ehm6eVqWVGPIc6609Djki5uIxzV4gicVz2q+aZQ3s/tKAN1UxbkmiMY+jY576L2Q
9dXElBQn3OqzRnr0YlrWRBInafMS8WGdcin6+jK3kYzgjD99MThC9iem431Pjzg5
Ox0vsAG1kNNF6Wmi0FiCejlEvW/0lpWb/foEXBs14E6fGF1jOFVEkqH82fIQt64c
6Egw241LFiNQqbuNDBKS9JKsAkktTQ+jzNLZAPw4qMcI3Q4NIU5zhkzAxHVKOx89
SJRFy66NXhIq+etfRC6B6kPfhpW82mRme8JXXwP00a4VtNrrMbYNC7l2TWpIC6J/
vDeHvoHhJftHkg5NtebN9vqOhNazncWLq7dfgZvaV425W4mZl1R42h9ktMa1Crhk
HBqzfhx8u0GCJwPAVo9AqndV2ahKLH3+WOfiwMGimzw=</Certificate>
                  <ServiceDescription/>
                  <TechnicalContactUrl/>
               </Endpoint>
            </ServiceEndpointList>
         </Process>
      </ProcessList>
   </ServiceInformation>
</ServiceMetadata>'

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A0088:ap1/services/urn:dk:healthcare:medcom:messaging:oioxml%3A%3Aurn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aoioxml%3Aschema%3Axsd%3Aclinicalemail%23urn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aoioxml%3Aschema%3Axsd%3Aclinicalemail%3Axdis91%3Axd9134l \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<ServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
   <ServiceInformation>
      <ParticipantIdentifier scheme="iso6523-actorid-upis">0088:ap1</ParticipantIdentifier>
      <DocumentIdentifier scheme="urn:dk:healthcare:medcom:messaging:oioxml">urn:dk:healthcare:medcom:messaging:oioxml:schema:xsd:clinicalemail#urn:dk:healthcare:medcom:messaging:oioxml:schema:xsd:clinicalemail:xdis91:xd9134l</DocumentIdentifier>
      <ProcessList>
         <Process>
            <ProcessIdentifier scheme="dk-messaging-procid">urn:www.digst.dk:profile:sdn-emergence</ProcessIdentifier>
            <ServiceEndpointList>
               <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                  <EndpointURI>http://domibus-ap1:8080/domibus/services/msh</EndpointURI>
                  <Certificate>MIIFRDCCAyygAwIBAgIJAKA2HL5nb4ZTMA0GCSqGSIb3DQEBCwUAMD8xCzAJBgNV
BAYTAkRLMRAwDgYDVQQKDAdUcmlmb3JrMRAwDgYDVQQLDAdUcmlmb3JrMQwwCgYD
VQQDDANhcDEwHhcNMjIwMTE3MDk1MzExWhcNMjcwNzEwMDk1MzExWjA/MQswCQYD
VQQGEwJESzEQMA4GA1UECgwHVHJpZm9yazEQMA4GA1UECwwHVHJpZm9yazEMMAoG
A1UEAwwDYXAxMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAzz+tQ57W
6E/bX0DRmTK29HOJ4nBh+fIuThWeJZJhwUzTXjgNXzTjrztOU9VkDfpD7DCJWTpa
QYZZajvD5t9/y4oeHP/02FR+F9vnPTMVjHQg6lFD+65chWOiq+QyZ4k8thmrnZCG
wRzSBAbJKls5iNohnkChxETb0CnT3Pj61q9Lt5tjDkzIsg9W46x2kjKW4WWzua9r
KxEKs7X+smPjvpW4crkFGCLTdKsQ9TOO7DBMV3V2Q2LjnzBS9JScPB7OkMwseSRO
b++1P9wK54CsPQ4kIKPjGNZmf3sFA0NmhV5PHpDZiwLhJXZukXIJZFGyfOQyVynD
V/PRQJUJf+UdPxJqRALcEne/2W5RC+69nar0mMU7wmcsDhQ0KhOZ2CiU/bsZhmz+
Wi0r3WLQFaDNJIXUjupQoDJH9bMq4LIoOn4JjEaoLGW7TZKazLfRzaVz9EPKYXPr
+AvgXxHMAZx2EfjwZRwJvXg9MnOlv33Kd88QpUXi9mhs8xjETHBeBoQLwrKGYs9Q
2cbAka8S/Y3EtX7dsHNfM09QQ6MX8XD1kZxJISa49h0J7X/u1CtoRzlJmaYNjJSN
KcyoBC1PSF3356TnAslIl74fPVQPT+nsvlwLc4sQge7/T7Mahws/CnKtYnGXIQjm
hugOaSJ0mU1kh6CMeProDCSQURnPF4a/aZkCAwEAAaNDMEEwDgYDVR0PAQH/BAQD
AgOIMBMGA1UdJQQMMAoGCCsGAQUFBwMBMBoGA1UdEQQTMBGCD3d3dy50cmlmb3Jr
LmNvbTANBgkqhkiG9w0BAQsFAAOCAgEAOEX7he1Fp1U7HvAKEZLAgYf8lRC1kHQQ
uVluDADJi0wyVYPBXQTdor3Msejl3znUqYjSHJeXKkKPxSPQfRJEq75265LAjAdz
t9WCKnyBMnPvrVoMITDY2OcMALk5dlo7qLPeTWOl6ibVQsAnXlEy31O0KuzNv51h
WS0x3Q/ZkBfjx9NIVgvU9qGqt0bvtuWKyE2j5suROQeBSlb6m7vE2lCH0EoxAehi
b7TebVrw7ohhMwj+hGSdVQTaC+x/PorAyE0e5uZoF1z9K2Kx3I375kDcGHRraw/1
F52RAhoSu7i3CtHr+p0nm5MR4bUgyjXJe+FbaiMmR03XCEo5SAFn74DWLKNeb/va
j6zHtvZpm6sRdydJxuAwBV51C8jqLADu8cgwZVzPUZ8tKRnnvKicNWCXUTo03FLk
2CB19efpvW4rRO/3BozMcG+tm+1vRdT9U81CvsYgH3xyTy0V0uIkGD3I2k1msWM/
G59RxsXY79cDwSjYzJ6b7pxE0rhkPTShIguMJtzjFo/K8dkzfu6A9RfzF8lDbTYN
bzn9VgPDWSlqOw24ljNv3o/z76gT8J/GPabgGJgd4uuy+I5aZC+B9OSHASaQm9ux
wpfT1EZfW4MVfurkw0pifxqMnhCYd0kIeyqHA137Q/MWSiNnim7ELPaM9xsn+cGD
QDvVJEGDqmU=</Certificate>
                  <ServiceDescription/>
                  <TechnicalContactUrl/>
               </Endpoint>
            </ServiceEndpointList>
         </Process>
      </ProcessList>
   </ServiceInformation>
</ServiceMetadata>'

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A0088:ap1/services/urn:dk:healthcare:medcom:messaging:ebxml%3A%3Aurn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aebxml%3Aschema%3Axsd%3Asbdhreceiptacknowledgement%23urn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aebxml%3Aschema%3Axsd%3Asbdhreceiptacknowledgement%3Aebbp-signals%3Aebbp-signals-2.0 \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<ServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
   <ServiceInformation>
      <ParticipantIdentifier scheme="iso6523-actorid-upis">0088:ap1</ParticipantIdentifier>
      <DocumentIdentifier scheme="urn:dk:healthcare:medcom:messaging:ebxml">urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgement#urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgement:ebbp-signals:ebbp-signals-2.0</DocumentIdentifier>
      <ProcessList>
         <Process>
            <ProcessIdentifier scheme="dk-messaging-procid">urn:www.digst.dk:profile:sdn-emergence</ProcessIdentifier>
            <ServiceEndpointList>
               <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                  <EndpointURI>http://domibus-ap1:8080/domibus/services/msh</EndpointURI>
                  <Certificate>MIIFRDCCAyygAwIBAgIJAKA2HL5nb4ZTMA0GCSqGSIb3DQEBCwUAMD8xCzAJBgNV
BAYTAkRLMRAwDgYDVQQKDAdUcmlmb3JrMRAwDgYDVQQLDAdUcmlmb3JrMQwwCgYD
VQQDDANhcDEwHhcNMjIwMTE3MDk1MzExWhcNMjcwNzEwMDk1MzExWjA/MQswCQYD
VQQGEwJESzEQMA4GA1UECgwHVHJpZm9yazEQMA4GA1UECwwHVHJpZm9yazEMMAoG
A1UEAwwDYXAxMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAzz+tQ57W
6E/bX0DRmTK29HOJ4nBh+fIuThWeJZJhwUzTXjgNXzTjrztOU9VkDfpD7DCJWTpa
QYZZajvD5t9/y4oeHP/02FR+F9vnPTMVjHQg6lFD+65chWOiq+QyZ4k8thmrnZCG
wRzSBAbJKls5iNohnkChxETb0CnT3Pj61q9Lt5tjDkzIsg9W46x2kjKW4WWzua9r
KxEKs7X+smPjvpW4crkFGCLTdKsQ9TOO7DBMV3V2Q2LjnzBS9JScPB7OkMwseSRO
b++1P9wK54CsPQ4kIKPjGNZmf3sFA0NmhV5PHpDZiwLhJXZukXIJZFGyfOQyVynD
V/PRQJUJf+UdPxJqRALcEne/2W5RC+69nar0mMU7wmcsDhQ0KhOZ2CiU/bsZhmz+
Wi0r3WLQFaDNJIXUjupQoDJH9bMq4LIoOn4JjEaoLGW7TZKazLfRzaVz9EPKYXPr
+AvgXxHMAZx2EfjwZRwJvXg9MnOlv33Kd88QpUXi9mhs8xjETHBeBoQLwrKGYs9Q
2cbAka8S/Y3EtX7dsHNfM09QQ6MX8XD1kZxJISa49h0J7X/u1CtoRzlJmaYNjJSN
KcyoBC1PSF3356TnAslIl74fPVQPT+nsvlwLc4sQge7/T7Mahws/CnKtYnGXIQjm
hugOaSJ0mU1kh6CMeProDCSQURnPF4a/aZkCAwEAAaNDMEEwDgYDVR0PAQH/BAQD
AgOIMBMGA1UdJQQMMAoGCCsGAQUFBwMBMBoGA1UdEQQTMBGCD3d3dy50cmlmb3Jr
LmNvbTANBgkqhkiG9w0BAQsFAAOCAgEAOEX7he1Fp1U7HvAKEZLAgYf8lRC1kHQQ
uVluDADJi0wyVYPBXQTdor3Msejl3znUqYjSHJeXKkKPxSPQfRJEq75265LAjAdz
t9WCKnyBMnPvrVoMITDY2OcMALk5dlo7qLPeTWOl6ibVQsAnXlEy31O0KuzNv51h
WS0x3Q/ZkBfjx9NIVgvU9qGqt0bvtuWKyE2j5suROQeBSlb6m7vE2lCH0EoxAehi
b7TebVrw7ohhMwj+hGSdVQTaC+x/PorAyE0e5uZoF1z9K2Kx3I375kDcGHRraw/1
F52RAhoSu7i3CtHr+p0nm5MR4bUgyjXJe+FbaiMmR03XCEo5SAFn74DWLKNeb/va
j6zHtvZpm6sRdydJxuAwBV51C8jqLADu8cgwZVzPUZ8tKRnnvKicNWCXUTo03FLk
2CB19efpvW4rRO/3BozMcG+tm+1vRdT9U81CvsYgH3xyTy0V0uIkGD3I2k1msWM/
G59RxsXY79cDwSjYzJ6b7pxE0rhkPTShIguMJtzjFo/K8dkzfu6A9RfzF8lDbTYN
bzn9VgPDWSlqOw24ljNv3o/z76gT8J/GPabgGJgd4uuy+I5aZC+B9OSHASaQm9ux
wpfT1EZfW4MVfurkw0pifxqMnhCYd0kIeyqHA137Q/MWSiNnim7ELPaM9xsn+cGD
QDvVJEGDqmU=</Certificate>
                  <ServiceDescription/>
                  <TechnicalContactUrl/>
               </Endpoint>
            </ServiceEndpointList>
         </Process>
      </ProcessList>
   </ServiceInformation>
</ServiceMetadata>'

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A0088:ap1/services/urn:dk:healthcare:medcom:messaging:ebxml%3A%3Aurn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aebxml%3Aschema%3Axsd%3Asbdhreceiptacknowledgementexception%23urn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aebxml%3Aschema%3Axsd%3Asbdhreceiptacknowledgementexception%3Aebbp-signals%3Aebbp-signals-2.0 \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<ServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
   <ServiceInformation>
      <ParticipantIdentifier scheme="iso6523-actorid-upis">0088:ap1</ParticipantIdentifier>
      <DocumentIdentifier scheme="urn:dk:healthcare:medcom:messaging:ebxml">urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgementexception#urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgementexception:ebbp-signals:ebbp-signals-2.0</DocumentIdentifier>
      <ProcessList>
         <Process>
            <ProcessIdentifier scheme="dk-messaging-procid">urn:www.digst.dk:profile:sdn-emergence</ProcessIdentifier>
            <ServiceEndpointList>
               <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                  <EndpointURI>http://domibus-ap1:8080/domibus/services/msh</EndpointURI>
                  <Certificate>MIIFRDCCAyygAwIBAgIJAKA2HL5nb4ZTMA0GCSqGSIb3DQEBCwUAMD8xCzAJBgNV
BAYTAkRLMRAwDgYDVQQKDAdUcmlmb3JrMRAwDgYDVQQLDAdUcmlmb3JrMQwwCgYD
VQQDDANhcDEwHhcNMjIwMTE3MDk1MzExWhcNMjcwNzEwMDk1MzExWjA/MQswCQYD
VQQGEwJESzEQMA4GA1UECgwHVHJpZm9yazEQMA4GA1UECwwHVHJpZm9yazEMMAoG
A1UEAwwDYXAxMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAzz+tQ57W
6E/bX0DRmTK29HOJ4nBh+fIuThWeJZJhwUzTXjgNXzTjrztOU9VkDfpD7DCJWTpa
QYZZajvD5t9/y4oeHP/02FR+F9vnPTMVjHQg6lFD+65chWOiq+QyZ4k8thmrnZCG
wRzSBAbJKls5iNohnkChxETb0CnT3Pj61q9Lt5tjDkzIsg9W46x2kjKW4WWzua9r
KxEKs7X+smPjvpW4crkFGCLTdKsQ9TOO7DBMV3V2Q2LjnzBS9JScPB7OkMwseSRO
b++1P9wK54CsPQ4kIKPjGNZmf3sFA0NmhV5PHpDZiwLhJXZukXIJZFGyfOQyVynD
V/PRQJUJf+UdPxJqRALcEne/2W5RC+69nar0mMU7wmcsDhQ0KhOZ2CiU/bsZhmz+
Wi0r3WLQFaDNJIXUjupQoDJH9bMq4LIoOn4JjEaoLGW7TZKazLfRzaVz9EPKYXPr
+AvgXxHMAZx2EfjwZRwJvXg9MnOlv33Kd88QpUXi9mhs8xjETHBeBoQLwrKGYs9Q
2cbAka8S/Y3EtX7dsHNfM09QQ6MX8XD1kZxJISa49h0J7X/u1CtoRzlJmaYNjJSN
KcyoBC1PSF3356TnAslIl74fPVQPT+nsvlwLc4sQge7/T7Mahws/CnKtYnGXIQjm
hugOaSJ0mU1kh6CMeProDCSQURnPF4a/aZkCAwEAAaNDMEEwDgYDVR0PAQH/BAQD
AgOIMBMGA1UdJQQMMAoGCCsGAQUFBwMBMBoGA1UdEQQTMBGCD3d3dy50cmlmb3Jr
LmNvbTANBgkqhkiG9w0BAQsFAAOCAgEAOEX7he1Fp1U7HvAKEZLAgYf8lRC1kHQQ
uVluDADJi0wyVYPBXQTdor3Msejl3znUqYjSHJeXKkKPxSPQfRJEq75265LAjAdz
t9WCKnyBMnPvrVoMITDY2OcMALk5dlo7qLPeTWOl6ibVQsAnXlEy31O0KuzNv51h
WS0x3Q/ZkBfjx9NIVgvU9qGqt0bvtuWKyE2j5suROQeBSlb6m7vE2lCH0EoxAehi
b7TebVrw7ohhMwj+hGSdVQTaC+x/PorAyE0e5uZoF1z9K2Kx3I375kDcGHRraw/1
F52RAhoSu7i3CtHr+p0nm5MR4bUgyjXJe+FbaiMmR03XCEo5SAFn74DWLKNeb/va
j6zHtvZpm6sRdydJxuAwBV51C8jqLADu8cgwZVzPUZ8tKRnnvKicNWCXUTo03FLk
2CB19efpvW4rRO/3BozMcG+tm+1vRdT9U81CvsYgH3xyTy0V0uIkGD3I2k1msWM/
G59RxsXY79cDwSjYzJ6b7pxE0rhkPTShIguMJtzjFo/K8dkzfu6A9RfzF8lDbTYN
bzn9VgPDWSlqOw24ljNv3o/z76gT8J/GPabgGJgd4uuy+I5aZC+B9OSHASaQm9ux
wpfT1EZfW4MVfurkw0pifxqMnhCYd0kIeyqHA137Q/MWSiNnim7ELPaM9xsn+cGD
QDvVJEGDqmU=</Certificate>
                  <ServiceDescription/>
                  <TechnicalContactUrl/>
               </Endpoint>
            </ServiceEndpointList>
         </Process>
      </ProcessList>
   </ServiceInformation>
</ServiceMetadata>'

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A0088:ap2/services/urn:dk:healthcare:medcom:messaging:oioxml%3A%3Aurn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aoioxml%3Aschema%3Axsd%3Aclinicalemail%23urn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aoioxml%3Aschema%3Axsd%3Aclinicalemail%3Axdis91%3Axd9134l \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<ServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
   <ServiceInformation>
      <ParticipantIdentifier scheme="iso6523-actorid-upis">0088:ap2</ParticipantIdentifier>
      <DocumentIdentifier scheme="urn:dk:healthcare:medcom:messaging:oioxml">urn:dk:healthcare:medcom:messaging:oioxml:schema:xsd:clinicalemail#urn:dk:healthcare:medcom:messaging:oioxml:schema:xsd:clinicalemail:xdis91:xd9134l</DocumentIdentifier>
      <ProcessList>
         <Process>
            <ProcessIdentifier scheme="dk-messaging-procid">urn:www.digst.dk:profile:sdn-emergence</ProcessIdentifier>
            <ServiceEndpointList>
               <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                  <EndpointURI>http://domibus-ap2:8080/domibus/services/msh</EndpointURI>
                  <Certificate>MIIFRDCCAyygAwIBAgIJAO3QrImA2f6SMA0GCSqGSIb3DQEBCwUAMD8xCzAJBgNV
BAYTAkRLMRAwDgYDVQQKDAdUcmlmb3JrMRAwDgYDVQQLDAdUcmlmb3JrMQwwCgYD
VQQDDANhcDIwHhcNMjIwMTE3MDk1MzIyWhcNMjcwNzEwMDk1MzIyWjA/MQswCQYD
VQQGEwJESzEQMA4GA1UECgwHVHJpZm9yazEQMA4GA1UECwwHVHJpZm9yazEMMAoG
A1UEAwwDYXAyMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA3xtqOzJl
58mjnBgs4wABUjKLbNdDPv0UX8iWVa4s6/O1blBcMKKXLxpdNThiYxI6NH1/GcXW
okmoLNMre3lJnn2JWPq3B0YSCioopz46bdNbKrmw66XGRA47Hooge8hCQYZBSvgq
lfvhLAoBsC3PdtZ2bo83K6oE6ffWcb6zkCXwD1U9CvXwR0axOTqz/enLgjKqUB+S
lUHoFV7QEDI1CNskXB0FDUGBACNcXDkQol0Zx3cvsL2TtbRpDeha+knU0dhjztid
TNlwdCPNyCmsN8db6uMUm0vbEu/cv95aYe/zuezHB4+YdmmBscRWWerlEBPC9Np9
NuARVFZlTYRPtIk6iqZvDd+mpESu+kOHrmwAx6HVHUlJZRpHziqY+X8gmfvstEnb
dmFDGH/q+WW+hOUChVO+yu1jyfpPtTucqBrgKHh3ZVEh7b4/3TfJpnPXina/ZRPy
CRN5/sAnQqbYxRwckoeouw0mX+uUbU+RBKCpWcUV0oXU7kaKjMWaJeHw7eqFtvH/
okb7j0sV1mzrJDvYxT/Vl6ESTnBqYNnEiI4ZCpJBncfF6ViDLBjFQOHf5gtvQoNo
Xfb3KuRjbdqSkRKvDmFZjtrrRF/rtmZy7zVxDI6iUaOw3Ff3w/YZrTqAA9ot5ZJ5
VhIw6u2m0MiyjiutwV8C+4YOXEOB3J1Q7vUCAwEAAaNDMEEwDgYDVR0PAQH/BAQD
AgOIMBMGA1UdJQQMMAoGCCsGAQUFBwMBMBoGA1UdEQQTMBGCD3d3dy50cmlmb3Jr
LmNvbTANBgkqhkiG9w0BAQsFAAOCAgEASYz+FvLWP+lgcV8ykqRDmGo1zsIJYQzQ
0xeUZyGCjzyVJnXQncROVPKj9Q474E1aD0Rzo7LFIY9FShFEXq7/VzApXkz2xCnV
laNu3RG7Lwq9lrGTKo8s92z4MInLNXDC+z6rUn5GdCGaG7kX9Z7Ua0fjfnATBzf6
2O1pI8okZ5f46Z+tekUoUa+bPZS+ee5GPX09UERL2lz6rCAwGfah14EhOmwuEwZ8
C3hchE4KXJRt+y00T64hvPOch4FFzY6OPwgcLoyOWooa7rv0wqzWZ9Oe7ETlPj2B
dkWsbgJbxns3QZAxNXz+2mgmpssUC4ZQigZs1HkAaboQt+hYkPIni3FWFBGon3ic
FNt0SLgd9ESekZLA+2wZkrRt8hoS+CzYZUzr8ksZUG+kp9rkafU+/JGrjrG9uvZW
eww9RQK/jRd/Ii666cn2GqPsvNyDOqJJuOlgauA+f1ZBwyy7dhzU0zfq2T+kmXF7
w0OQYnQXHsif1vErijGCi3tvrLzZqQcI0kU3zhY+aLtuxoErLqin1acaZ+LQSFgT
ngYNqaYP8wvw7lfR0MHg8J20c2PEt67QbYdUEzYmAzuY2+DanHYKMi+JLW5MVmv1
OHl/itZ9IxjzSckly2wlVI9NhGYvUu/4xtv8md+7uT7z0weJHuDWIZ/4Kyqc3lu8
mi16FSwUefQ=</Certificate>
                  <ServiceDescription/>
                  <TechnicalContactUrl/>
               </Endpoint>
            </ServiceEndpointList>
         </Process>
      </ProcessList>
   </ServiceInformation>
</ServiceMetadata>'

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A0088:ap2/services/urn:dk:healthcare:medcom:messaging:ebxml%3A%3Aurn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aebxml%3Aschema%3Axsd%3Asbdhreceiptacknowledgement%23urn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aebxml%3Aschema%3Axsd%3Asbdhreceiptacknowledgement%3Aebbp-signals%3Aebbp-signals-2.0 \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<ServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
   <ServiceInformation>
      <ParticipantIdentifier scheme="iso6523-actorid-upis">0088:ap2</ParticipantIdentifier>
      <DocumentIdentifier scheme="urn:dk:healthcare:medcom:messaging:ebxml">urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgement#urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgement:ebbp-signals:ebbp-signals-2.0</DocumentIdentifier>
      <ProcessList>
         <Process>
            <ProcessIdentifier scheme="dk-messaging-procid">urn:www.digst.dk:profile:sdn-emergence</ProcessIdentifier>
            <ServiceEndpointList>
               <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                  <EndpointURI>http://domibus-ap2:8080/domibus/services/msh</EndpointURI>
                  <Certificate>MIIFRDCCAyygAwIBAgIJAO3QrImA2f6SMA0GCSqGSIb3DQEBCwUAMD8xCzAJBgNV
BAYTAkRLMRAwDgYDVQQKDAdUcmlmb3JrMRAwDgYDVQQLDAdUcmlmb3JrMQwwCgYD
VQQDDANhcDIwHhcNMjIwMTE3MDk1MzIyWhcNMjcwNzEwMDk1MzIyWjA/MQswCQYD
VQQGEwJESzEQMA4GA1UECgwHVHJpZm9yazEQMA4GA1UECwwHVHJpZm9yazEMMAoG
A1UEAwwDYXAyMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA3xtqOzJl
58mjnBgs4wABUjKLbNdDPv0UX8iWVa4s6/O1blBcMKKXLxpdNThiYxI6NH1/GcXW
okmoLNMre3lJnn2JWPq3B0YSCioopz46bdNbKrmw66XGRA47Hooge8hCQYZBSvgq
lfvhLAoBsC3PdtZ2bo83K6oE6ffWcb6zkCXwD1U9CvXwR0axOTqz/enLgjKqUB+S
lUHoFV7QEDI1CNskXB0FDUGBACNcXDkQol0Zx3cvsL2TtbRpDeha+knU0dhjztid
TNlwdCPNyCmsN8db6uMUm0vbEu/cv95aYe/zuezHB4+YdmmBscRWWerlEBPC9Np9
NuARVFZlTYRPtIk6iqZvDd+mpESu+kOHrmwAx6HVHUlJZRpHziqY+X8gmfvstEnb
dmFDGH/q+WW+hOUChVO+yu1jyfpPtTucqBrgKHh3ZVEh7b4/3TfJpnPXina/ZRPy
CRN5/sAnQqbYxRwckoeouw0mX+uUbU+RBKCpWcUV0oXU7kaKjMWaJeHw7eqFtvH/
okb7j0sV1mzrJDvYxT/Vl6ESTnBqYNnEiI4ZCpJBncfF6ViDLBjFQOHf5gtvQoNo
Xfb3KuRjbdqSkRKvDmFZjtrrRF/rtmZy7zVxDI6iUaOw3Ff3w/YZrTqAA9ot5ZJ5
VhIw6u2m0MiyjiutwV8C+4YOXEOB3J1Q7vUCAwEAAaNDMEEwDgYDVR0PAQH/BAQD
AgOIMBMGA1UdJQQMMAoGCCsGAQUFBwMBMBoGA1UdEQQTMBGCD3d3dy50cmlmb3Jr
LmNvbTANBgkqhkiG9w0BAQsFAAOCAgEASYz+FvLWP+lgcV8ykqRDmGo1zsIJYQzQ
0xeUZyGCjzyVJnXQncROVPKj9Q474E1aD0Rzo7LFIY9FShFEXq7/VzApXkz2xCnV
laNu3RG7Lwq9lrGTKo8s92z4MInLNXDC+z6rUn5GdCGaG7kX9Z7Ua0fjfnATBzf6
2O1pI8okZ5f46Z+tekUoUa+bPZS+ee5GPX09UERL2lz6rCAwGfah14EhOmwuEwZ8
C3hchE4KXJRt+y00T64hvPOch4FFzY6OPwgcLoyOWooa7rv0wqzWZ9Oe7ETlPj2B
dkWsbgJbxns3QZAxNXz+2mgmpssUC4ZQigZs1HkAaboQt+hYkPIni3FWFBGon3ic
FNt0SLgd9ESekZLA+2wZkrRt8hoS+CzYZUzr8ksZUG+kp9rkafU+/JGrjrG9uvZW
eww9RQK/jRd/Ii666cn2GqPsvNyDOqJJuOlgauA+f1ZBwyy7dhzU0zfq2T+kmXF7
w0OQYnQXHsif1vErijGCi3tvrLzZqQcI0kU3zhY+aLtuxoErLqin1acaZ+LQSFgT
ngYNqaYP8wvw7lfR0MHg8J20c2PEt67QbYdUEzYmAzuY2+DanHYKMi+JLW5MVmv1
OHl/itZ9IxjzSckly2wlVI9NhGYvUu/4xtv8md+7uT7z0weJHuDWIZ/4Kyqc3lu8
mi16FSwUefQ=</Certificate>
                  <ServiceDescription/>
                  <TechnicalContactUrl/>
               </Endpoint>
            </ServiceEndpointList>
         </Process>
      </ProcessList>
   </ServiceInformation>
</ServiceMetadata>'

curl --request PUT \
--url ${url}iso6523-actorid-upis%3A%3A0088:ap2/services/urn:dk:healthcare:medcom:messaging:ebxml%3A%3Aurn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aebxml%3Aschema%3Axsd%3Asbdhreceiptacknowledgementexception%23urn%3Adk%3Ahealthcare%3Amedcom%3Amessaging%3Aebxml%3Aschema%3Axsd%3Asbdhreceiptacknowledgementexception%3Aebbp-signals%3Aebbp-signals-2.0 \
-u ${username}:${password} \
--header 'content-type: application/xml' \
--data '<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<ServiceMetadata xmlns="http://docs.oasis-open.org/bdxr/ns/SMP/2016/05">
   <ServiceInformation>
      <ParticipantIdentifier scheme="iso6523-actorid-upis">0088:ap2</ParticipantIdentifier>
      <DocumentIdentifier scheme="urn:dk:healthcare:medcom:messaging:ebxml">urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgementexception#urn:dk:healthcare:medcom:messaging:ebxml:schema:xsd:sbdhreceiptacknowledgementexception:ebbp-signals:ebbp-signals-2.0</DocumentIdentifier>
      <ProcessList>
         <Process>
            <ProcessIdentifier scheme="dk-messaging-procid">urn:www.digst.dk:profile:sdn-emergence</ProcessIdentifier>
            <ServiceEndpointList>
               <Endpoint transportProfile="bdxr-transport-ebms3-as4-v1p0">
                  <EndpointURI>http://domibus-ap2:8080/domibus/services/msh</EndpointURI>
                  <Certificate>MIIFRDCCAyygAwIBAgIJAO3QrImA2f6SMA0GCSqGSIb3DQEBCwUAMD8xCzAJBgNV
BAYTAkRLMRAwDgYDVQQKDAdUcmlmb3JrMRAwDgYDVQQLDAdUcmlmb3JrMQwwCgYD
VQQDDANhcDIwHhcNMjIwMTE3MDk1MzIyWhcNMjcwNzEwMDk1MzIyWjA/MQswCQYD
VQQGEwJESzEQMA4GA1UECgwHVHJpZm9yazEQMA4GA1UECwwHVHJpZm9yazEMMAoG
A1UEAwwDYXAyMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA3xtqOzJl
58mjnBgs4wABUjKLbNdDPv0UX8iWVa4s6/O1blBcMKKXLxpdNThiYxI6NH1/GcXW
okmoLNMre3lJnn2JWPq3B0YSCioopz46bdNbKrmw66XGRA47Hooge8hCQYZBSvgq
lfvhLAoBsC3PdtZ2bo83K6oE6ffWcb6zkCXwD1U9CvXwR0axOTqz/enLgjKqUB+S
lUHoFV7QEDI1CNskXB0FDUGBACNcXDkQol0Zx3cvsL2TtbRpDeha+knU0dhjztid
TNlwdCPNyCmsN8db6uMUm0vbEu/cv95aYe/zuezHB4+YdmmBscRWWerlEBPC9Np9
NuARVFZlTYRPtIk6iqZvDd+mpESu+kOHrmwAx6HVHUlJZRpHziqY+X8gmfvstEnb
dmFDGH/q+WW+hOUChVO+yu1jyfpPtTucqBrgKHh3ZVEh7b4/3TfJpnPXina/ZRPy
CRN5/sAnQqbYxRwckoeouw0mX+uUbU+RBKCpWcUV0oXU7kaKjMWaJeHw7eqFtvH/
okb7j0sV1mzrJDvYxT/Vl6ESTnBqYNnEiI4ZCpJBncfF6ViDLBjFQOHf5gtvQoNo
Xfb3KuRjbdqSkRKvDmFZjtrrRF/rtmZy7zVxDI6iUaOw3Ff3w/YZrTqAA9ot5ZJ5
VhIw6u2m0MiyjiutwV8C+4YOXEOB3J1Q7vUCAwEAAaNDMEEwDgYDVR0PAQH/BAQD
AgOIMBMGA1UdJQQMMAoGCCsGAQUFBwMBMBoGA1UdEQQTMBGCD3d3dy50cmlmb3Jr
LmNvbTANBgkqhkiG9w0BAQsFAAOCAgEASYz+FvLWP+lgcV8ykqRDmGo1zsIJYQzQ
0xeUZyGCjzyVJnXQncROVPKj9Q474E1aD0Rzo7LFIY9FShFEXq7/VzApXkz2xCnV
laNu3RG7Lwq9lrGTKo8s92z4MInLNXDC+z6rUn5GdCGaG7kX9Z7Ua0fjfnATBzf6
2O1pI8okZ5f46Z+tekUoUa+bPZS+ee5GPX09UERL2lz6rCAwGfah14EhOmwuEwZ8
C3hchE4KXJRt+y00T64hvPOch4FFzY6OPwgcLoyOWooa7rv0wqzWZ9Oe7ETlPj2B
dkWsbgJbxns3QZAxNXz+2mgmpssUC4ZQigZs1HkAaboQt+hYkPIni3FWFBGon3ic
FNt0SLgd9ESekZLA+2wZkrRt8hoS+CzYZUzr8ksZUG+kp9rkafU+/JGrjrG9uvZW
eww9RQK/jRd/Ii666cn2GqPsvNyDOqJJuOlgauA+f1ZBwyy7dhzU0zfq2T+kmXF7
w0OQYnQXHsif1vErijGCi3tvrLzZqQcI0kU3zhY+aLtuxoErLqin1acaZ+LQSFgT
ngYNqaYP8wvw7lfR0MHg8J20c2PEt67QbYdUEzYmAzuY2+DanHYKMi+JLW5MVmv1
OHl/itZ9IxjzSckly2wlVI9NhGYvUu/4xtv8md+7uT7z0weJHuDWIZ/4Kyqc3lu8
mi16FSwUefQ=</Certificate>
                  <ServiceDescription/>
                  <TechnicalContactUrl/>
               </Endpoint>
            </ServiceEndpointList>
         </Process>
      </ProcessList>
   </ServiceInformation>
</ServiceMetadata>'

