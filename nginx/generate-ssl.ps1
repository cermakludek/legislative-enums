# PowerShell script to generate self-signed SSL certificate for development
# For production, use Let's Encrypt or other trusted CA

$SSL_DIR = Join-Path $PSScriptRoot "ssl"
New-Item -ItemType Directory -Force -Path $SSL_DIR | Out-Null

# Certificate parameters
$certParams = @{
    Subject = "CN=localhost"
    DnsName = @("localhost", "*.localhost")
    KeyAlgorithm = "RSA"
    KeyLength = 2048
    NotAfter = (Get-Date).AddYears(1)
    CertStoreLocation = "Cert:\CurrentUser\My"
    KeyExportPolicy = "Exportable"
    KeyUsage = @("KeyEncipherment", "DigitalSignature")
    Type = "SSLServerAuthentication"
}

try {
    # Generate certificate
    $cert = New-SelfSignedCertificate @certParams

    # Export certificate (PEM format)
    $certPath = Join-Path $SSL_DIR "cert.pem"
    $keyPath = Join-Path $SSL_DIR "key.pem"

    # Export as PFX first
    $pfxPath = Join-Path $SSL_DIR "cert.pfx"
    $password = ConvertTo-SecureString -String "temp123" -Force -AsPlainText
    Export-PfxCertificate -Cert $cert -FilePath $pfxPath -Password $password | Out-Null

    # Convert PFX to PEM using openssl (if available)
    if (Get-Command openssl -ErrorAction SilentlyContinue) {
        openssl pkcs12 -in $pfxPath -out $certPath -nokeys -password pass:temp123
        openssl pkcs12 -in $pfxPath -out $keyPath -nocerts -nodes -password pass:temp123
        Remove-Item $pfxPath -Force
        Write-Host "SSL certificate generated successfully!" -ForegroundColor Green
        Write-Host "Certificate: $certPath"
        Write-Host "Private key: $keyPath"
    } else {
        Write-Host "OpenSSL not found. Certificate exported as PFX: $pfxPath" -ForegroundColor Yellow
        Write-Host "Install OpenSSL or use WSL to convert to PEM format."
    }

    # Remove from certificate store
    Remove-Item "Cert:\CurrentUser\My\$($cert.Thumbprint)" -Force

    Write-Host ""
    Write-Host "Note: This is a self-signed certificate for development." -ForegroundColor Yellow
    Write-Host "For production, use a certificate from a trusted CA."
}
catch {
    Write-Host "Error generating SSL certificate: $_" -ForegroundColor Red
    exit 1
}
