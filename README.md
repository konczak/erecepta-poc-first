# PoC for first step of integration with e-recepta
Simple project which creates Web server exposing HTTPS and forcing user authentication using certificate.

Java project sends request to Web server using truststore with self-singed certificate of CA and keystore with client certificate.

# Certificate Based Authentication

Thank you to https://github.com/andreburgaud/docker-cert-auth which explains how to setup such simple Web server. README copied from their page.

WARN: makefile has been updated to server project purpose.

* **Docker**: Containerization platform (https://www.docker.com/)
* **NGINX**: Web server (https://www.nginx.com/)
* **Curl**: Command line tool to interact with web server (https://curl.haxx.se/)
* **OpenSSL**: TLS and SSL implementation (https://www.openssl.org/)

# Getting Started

## Pull the docker image from Docker Hub:

```
$ docker pull andreburgaud/nginx-cert-auth
```

## Create the keys and certificates:

You can create the keys and certificates either manually, or via a `Makefile` (recommended). In both cases, export an environment variable `PASSPHRASE` to the value of the passphrase used by the **OpenSSL** commands.

After generating the keys and certificates you will have a directory named `certs` containing keys and certificates needed to run the container and communicate securely with the server.

WARN: Makefile contains hardcode serial IDs for each certificate what can result with unexpected behaviour when stored in OS trusted list.

### Makefile

`Makefile` is in poc folder. You will need to update some paths. Also certificates subjects are adjusted to support Bash in Windows.

```
$ export PASSPHRASE=<some_passphrase>
$ make certs
```

will create required certificates.

## Start the Container

To start the container with a volume pointing to the local `certs` directory:

```
$ docker run --rm -p 8443:443 -v`pwd`/poc/certs/:/etc/nginx/certs/ andreburgaud/nginx-cert-auth
```

The server can also be started as a `daemon` with option `-d`, but for the purpose of testing this development environment, it is more convenient to check the output of the server in the terminal.

## Tests using Curl

From a different terminal, you can use **curl** to perform some manual tests:

With `-k` **curl** proceeds even if the server connection is considered insecure (i.e. server certificate validation fails)

```
$ curl -v -s -k --key certs/client.key --cert certs/client.crt https://localhost:8443
```

To test the server certificate validation by the client, replace `-k` with `--cacert certs/ca.crt`. The server certificate was signed with `ca.crt` and this will allow the client to perform a successful validation. This requires that the `hostname` matches the name of the host in the certificate (`example.com`). Adding the host name to the `/etc/hosts` file of your system allows to successfully test this scenario on a development system.

```
$ curl -v -s --cacert certs/ca.crt --key certs/client.key --cert certs/client.crt https://example.com:8443
```

In both of those tests, you should get a response with Subject DN of the certificate that was passed on by NGINX to the internal web server upon validation of the client certificate.

## Requirements

The following software need to be installed on the system to build and test the project:

* Docker
* OpenSSL
* curl
* make

# Keys and Certs

## Create CA Key and Certificate for signing Client Certs

**CA**: Certificate Authority

```
$ openssl genrsa -aes256 -out ca.key 4096                   # CA key
$ openssl req -new -x509 -days 365 -key ca.key -out ca.crt  # CA Certificate
```

## Create the Server Key and CSR

**CSR** : Certificate Signing Request

```
$ openssl genrsa -aes256 -out server.key 2048       # Server key
$ openssl req -new -key server.key -out server.csr  # Server CSR
```

## Create and Sign Server Certificate

* Use a real CA certificate for production
* If you want to test the validation of the server certificate by the server, in the CN of the certificate subject, enter the hostname you want to use for the test (check the Makefile for reference; in the Makefile and in this document the host is `example.com`).

```
$ openssl x509 -req -days 365 -in server.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out server.crt
```

## Create Client Key and CSR

```
$ openssl genrsa -aes256 -out client.key 2048       # Client key
$ openssl req -new -key client.key -out client.csr  # Client CSR
```

## Create and Sign Client Certificate

* Using the same CA cert as for the server, in the context of this setup. In a production environment, the client certificate could be self-signed, but the server certificate would be signed by a trusted CA (e.g. Digicert, GoDaddy, Network Solutions, Let's Encrypt...).

```
$ openssl x509 -req -days 365 -in client.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out client.crt
```

## Remove passphrases

This is needed so that passphrases are not requested during the start or restart of a service.

```
$ openssl rsa -in server.key -out server.key
$ openssl rsa -in client.key -out client.key
```

# Useful Docker Commands

See Docker commands in the Makefile.

```
$ docker build -t nginx-cert-auth .                                             # Build image
$ docker run --rm -p 8443:443 -v`pwd`/certs/:/etc/nginx/certs/ nginx-cert-auth  # Start container with certs in a volume
$ docker run --rm -p 8443:443 nginx-cert-auth                                   # Start container with certs in the container
$ docker ps                                                                     # List running active container
$ docker ps -a                                                                  # List all active container
$ docker rm $(docker ps -a -f status=exited -f status=created -q)               # Delete all containers
```

To stop the container, you can either press `[CTRL+C]` in the terminal where the container was started (assuming the container was not started with the *daemon* option `-d`), or execute `docker stop <container_name>` after finding the container name via `docker ps`.

# Development

To build the image locally:

```
$ git clone https://github.com/andreburgaud/nginx-cert-auth
$ cd docker-cert-auth
$ make all
```

By default, the generated Docker image is `nginx-cert-auth`. It can be changed in the `Makefile` by changing the variable name `IMAGE`.
