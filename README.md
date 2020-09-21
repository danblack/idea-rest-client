# Rest Client

![Build](https://github.com/danblack/idea-rest-client/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/9232-http-editor-client.svg)](https://plugins.jetbrains.com/plugin/9232-http-editor-client)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/9232-http-editor-client.svg)](https://plugins.jetbrains.com/plugin/9232-http-editor-client)

<!-- Plugin description -->
Allows making http requests using IDEA text editor

## Features
- Supports GET, POST, PUT, DELETE, PATCH, HEAD & OPTIONS requests
- Multiline request parameters
- Response auto format (based on response Content-Type header)
- Customized colors and fonts
- Customized keyboard shortcuts
- Comments

![demo](doc/demo.png)

## YouTube demos 

* [Simple demo](https://www.youtube.com/watch?v=AliJaGmXlxc)

```bash
http://localhost:8080/test
@Content-type: application/json
%%%

# Duration: 539 ms
# URL: http://localhost:8080/test

# HTTP/1.1 200

@Content-Type: application/json;charset=UTF-8
@Transfer-Encoding: chunked
@Date: Fri, 04 Nov 2016 08:51:00 GMT

{
  "id": 2,
  "name": "resp name",
  "item": [
    {
      "id": 21,
      "name": "item 1",
      "price": 1
    },
    {
      "id": 22,
      "name": "item 2",
      "price": 10
    }
  ]
}
```

* [POST demo](https://www.youtube.com/watch?v=s4oVpfAJHFM)

```bash
POST
http://localhost:8080/mirror-post
@Content-Type: application/json
{"name":"JsonDan"}
%%%

# Duration: 501 ms
# URL: http://localhost:8080/mirror-post

# HTTP/1.1 200

@Content-Type: application/json;charset=UTF-8
@Transfer-Encoding: chunked
@Date: Fri, 04 Nov 2016 11:59:34 GMT

{
  "name": "JsonDan"
}
```

* [Request params demo](https://www.youtube.com/watch?v=Gt8OnWPiJUY)

```bash
http://localhost:8080/mirror-params?one=1
#&two=2&three=3
#&four=4
&five=5
%%%

# Duration: 666 ms
# URL: http://localhost:8080/mirror-params?one=1&five=5

# HTTP/1.1 200

@Content-Type: text/plain;charset=ISO-8859-1
@Content-Length: 14
@Date: Fri, 04 Nov 2016 12:19:52 GMT

one: 1
five: 5
```

<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "%NAME%"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/%REPOSITORY%/releases/latest) and install it manually using
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>
