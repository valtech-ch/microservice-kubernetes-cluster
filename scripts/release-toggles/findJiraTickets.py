#!/usr/bin/env python
import requests, json, sys;

url = 'https://tracking.valtech.swiss/rest/api/latest/search?jql=project=VJAP+and+status+in+(%22Internal%20review%22%2C%20%22Ready%20for%20internal%20review%22)'
headers = {'Authorization': 'Bearer ' + sys.argv[1]}
respone = requests.get(url, headers=headers)

obj = json.loads(respone.text)

s = ","
tickets = []
for issue in obj['issues']:
  tickets.append(issue['key'])

print(s.join(tickets))