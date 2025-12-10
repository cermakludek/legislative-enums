#!/bin/bash

# Script to generate self-signed SSL certificate for development
# For production, use Let's Encrypt or other trusted CA

SSL_DIR="$(dirname "$0")/ssl"
mkdir -p "$SSL_DIR"

# Certificate validity (days)
DAYS=365

# Generate private key and certificate
openssl req -x509 -nodes -days $DAYS -newkey rsa:2048 \
    -keyout "$SSL_DIR/key.pem" \
    -out "$SSL_DIR/cert.pem" \
    -subj "/C=CZ/ST=Czech Republic/L=Prague/O=Legislative Codelists/OU=IT/CN=localhost" \
    -addext "subjectAltName=DNS:localhost,DNS:*.localhost,IP:127.0.0.1"

if [ $? -eq 0 ]; then
    echo "SSL certificate generated successfully!"
    echo "Certificate: $SSL_DIR/cert.pem"
    echo "Private key: $SSL_DIR/key.pem"
    echo ""
    echo "Note: This is a self-signed certificate for development."
    echo "For production, use a certificate from a trusted CA."
else
    echo "Error generating SSL certificate"
    exit 1
fi
