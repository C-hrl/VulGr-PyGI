$processName = $args[0]

$id = $PID
while ($true) {
  $p = Get-CimInstance -Class Win32_Process -Filter "ProcessId = $id"
  if ($null -eq $p) {
    throw "Could not determine $processName process"
  }
  Write-Host "Found process: $p"
  if ($p[0].Name -eq "$processName") {
    Break
  }
  else {
    $id = $p[0].ParentProcessId
  }
}
Write-Host "Injecting into process: $p"
Start-Process -FilePath ".\tracer.exe" -Wait -NoNewWindow -ArgumentList "--inject=$id"
