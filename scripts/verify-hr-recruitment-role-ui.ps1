$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot

function Read-ProjectFile {
    param([string] $RelativePath)
    return Get-Content -Raw -LiteralPath (Join-Path $root $RelativePath)
}

function Assert-DoesNotContain {
    param(
        [string] $Content,
        [string] $Needle,
        [string] $Message
    )
    if ($Content.Contains($Needle)) {
        throw $Message
    }
}

function Assert-Contains {
    param(
        [string] $Content,
        [string] $Needle,
        [string] $Message
    )
    if (-not $Content.Contains($Needle)) {
        throw $Message
    }
}

$hrManagerPages = @(
    "src/main/webapp/Views/hr/HrHome.jsp",
    "src/main/webapp/Views/hr/PayrollManagement.jsp",
    "src/main/webapp/Views/hr/EmployeeList.jsp",
    "src/main/webapp/Views/hr/CreateEmployee.jsp",
    "src/main/webapp/Views/hr/ViewRecruitment.jsp"
)

foreach ($page in $hrManagerPages) {
    $content = Read-ProjectFile $page
    Assert-DoesNotContain $content '${pageContext.request.contextPath}/candidates' "$page must not link to HR Staff candidate list."
    Assert-DoesNotContain $content '${pageContext.request.contextPath}/postRecruitments' "$page must not link to HR Staff recruitment-post management."
    Assert-DoesNotContain $content '${pageContext.request.contextPath}/postRecruitment"' "$page contains the old broken /postRecruitment link."
}

$hrStaffHome = Read-ProjectFile "src/main/webapp/Views/HrStaff/HrStaffHome.jsp"
$hrStaffPostRecruitment = Read-ProjectFile "src/main/webapp/Views/HrStaff/PostRecruitment.jsp"
$hrStaffViewCandidate = Read-ProjectFile "src/main/webapp/Views/HrStaff/ViewCandidate.jsp"
$viewCvController = Read-ProjectFile "src/main/java/com/hrm/controller/hr/ViewCV.java"
$detailRecruitmentController = Read-ProjectFile "src/main/java/com/hrm/controller/hr/DetailRecruitment.java"

Assert-Contains $hrStaffHome '${pageContext.request.contextPath}/postRecruitments' "HR Staff home must link to recruitment-post management."
Assert-Contains $hrStaffHome '${pageContext.request.contextPath}/candidates' "HR Staff home must link to candidate management."
Assert-Contains $hrStaffPostRecruitment '${pageContext.request.contextPath}/detailRecruitmentCreate' "HR Staff recruitment page must link to create recruitment."
Assert-Contains $hrStaffViewCandidate '${pageContext.request.contextPath}/postRecruitments' "HR Staff candidate page must link back to recruitment posts."
Assert-Contains $viewCvController 'PermissionUtil.ROLE_HR_STAFF' "ViewCV controller must be restricted to HR Staff."
Assert-Contains $detailRecruitmentController 'PermissionUtil.ROLE_HR_STAFF' "DetailRecruitment controller must be restricted to HR Staff."

Write-Host "HR recruitment role UI checks passed."
