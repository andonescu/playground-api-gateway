# Problem

The API gateway needs to filter users by IP based on a blacklist. 

The system needs to have means to add, delete, get blacklist configurations and to check the IP that does the request to drop it.


GET /api/ips?size=&page=

{
  "pageInfo": {
    "page": 1,  // TODO: later on
    "size": 2, // TODO: later on
    "total": 9
  },
  "links": [ // hateoas - later on
    {
      "rel": "next",
      "href": "/api/ips?page=2&size=2"
    },
    {
      "rel": "last",
      "href": "/api/ips?page=5&size=2"
    },
    {
      "rel": "self",
      "href": "/api/ips?page=5&size=2"
    }
  ],
  "data": [
    {
      "ip": "192.168.2.1",
      "links": [
        {
          "rel": "self",
          "href": "/api/ips/192.168.2.1"
        }
      ]
    },
    {
      "ip": "192.168.2.4",
      "links": [
        {
          "rel": "self",
          "href": "/api/ips/192.168.2.4"
        }
      ]
    }
  ]
}

DELETE /api/ips/192.168.4.2

POST /api/ips 
{
    "ip": "192.168.4.2"
}

Return 201 + Location Header : /api/ips/192.168.4.2

errors:

{
  "errors": [
    {
      "field": "ip",
      "message": "Already exists"
    }
  ]
}

IPV6 ???