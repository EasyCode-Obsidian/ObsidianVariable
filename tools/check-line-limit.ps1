param(
    [int]$MaxLines = 199,
    [string]$Root = "src/main/kotlin"
)

$errors = @()
$files = Get-ChildItem -Path $Root -Recurse -Filter *.kt -File

foreach ($file in $files) {
    $lineCount = (Get-Content -Path $file.FullName | Measure-Object -Line).Lines
    if ($lineCount -gt $MaxLines) {
        $errors += "{0} => {1} lines" -f $file.FullName, $lineCount
    }
}

if ($errors.Count -gt 0) {
    Write-Host "Line limit check failed:" -ForegroundColor Red
    $errors | ForEach-Object { Write-Host $_ -ForegroundColor Red }
    exit 1
}

Write-Host "Line limit check passed. Max allowed: $MaxLines" -ForegroundColor Green
exit 0
