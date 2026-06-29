$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot

function Read-ProjectFile {
    param([string] $RelativePath)
    return Get-Content -Raw -LiteralPath (Join-Path $root $RelativePath)
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
    "src/main/webapp/Views/hr/ViewRecruitment.jsp",
    "src/main/webapp/Views/hr/CreateEmployee.jsp",
    "src/main/webapp/Views/hr/EmployeeList.jsp",
    "src/main/webapp/Views/hr/PayrollManagement.jsp",
    "src/main/webapp/Views/hr/ApproveRejectContract.jsp"
)

foreach ($page in $hrManagerPages) {
    $content = Read-ProjectFile $page
    Assert-Contains $content '<%@ include file="_HrManagerSidebar.jspf" %>' "$page must statically include the shared HR Manager sidebar fragment."
    Assert-Contains $content 'hrSidebarActive' "$page must set hrSidebarActive before the shared sidebar fragment."
    if ($content.Contains('<jsp:include page="/Views/hr/_HrManagerSidebar.jsp"') -or
        $content.Contains('<jsp:include page="/Views/hr/_HrManagerSidebar.jspf"')) {
        throw "$page must not dynamically include the HR Manager sidebar because dynamic fragment includes can stop rendering after the header."
    }
    if ($page -ne "src/main/webapp/Views/hr/HrHome.jsp") {
        Assert-Contains $content 'hr-manager-page-shell' "$page must use the shared HR Manager page shell."
        Assert-Contains $content 'hr-theme.css?v=hr-manager-shell-20260627-4' "$page must load the cache-busted shared HR theme stylesheet."
    }
    if ($content.Contains('/Views/hr/HrHome.jsp')) {
        throw "$page must route back to /HrHomeController instead of directly opening HrHome.jsp."
    }
}

$sidebar = Read-ProjectFile "src/main/webapp/Views/hr/_HrManagerSidebar.jspf"
Assert-Contains $sidebar 'pageEncoding="UTF-8"' "Shared HR Manager sidebar fragment must declare UTF-8 pageEncoding so Vietnamese text is not mojibake after static include."
if ($sidebar.Contains('contentType=')) {
    throw "Shared HR Manager sidebar fragment must not declare contentType; parent JSPs own response content type."
}
if (Test-Path -LiteralPath (Join-Path $root "src/main/webapp/Views/hr/_HrManagerSidebar.jsp")) {
    throw "Old dynamic _HrManagerSidebar.jsp must not exist; use _HrManagerSidebar.jspf as a static fragment."
}
$expectedSidebarSnippets = @(
    'hr-sidebar-brand',
    'brand-mark',
    'hrSidebarActive eq ''overview''',
    'hrSidebarActive eq ''requests''',
    'hrSidebarActive eq ''payroll''',
    'hrSidebarActive eq ''contracts''',
    'hrSidebarActive eq ''recruitment''',
    'hrSidebarActive eq ''create-employee''',
    'hrSidebarActive eq ''employee-list''',
    'hrSidebarActive eq ''reports''',
    '/HrHomeController',
    '/HrHomeController?section=requests-approval',
    '/hr/payroll-approval',
    '/hr/approve-reject-contracts',
    '/viewRecruitment',
    '/hr/create-employee',
    '/hr/employee-list',
    '/HrHomeController?section=reports-analytics'
)
foreach ($snippet in $expectedSidebarSnippets) {
    Assert-Contains $sidebar $snippet "Shared HR Manager sidebar must contain snippet '$snippet'."
}

$theme = Read-ProjectFile "src/main/webapp/css/hr-theme.css"
Assert-Contains $theme '.hr-manager-page-shell' "hr-theme.css must define the shared HR Manager page shell."
Assert-Contains $theme '.hr-manager-page-shell .hr-sidebar' "hr-theme.css must style the shared HR Manager sidebar."
Assert-Contains $theme '.hr-manager-page-shell .hr-sidebar-brand' "hr-theme.css must style the actual shared HR Manager sidebar brand."
Assert-Contains $theme '.hr-manager-page-shell .hr-content-area' "hr-theme.css must style the shared HR Manager content area."
Assert-Contains $theme '.hr-manager-page-shell .card' "hr-theme.css must normalize child page cards."
Assert-Contains $theme '.hr-manager-page-shell .hr-sidebar::before' "hr-theme.css must disable the old pseudo sidebar brand."
Assert-Contains $theme 'content: none !important;' "hr-theme.css must remove the old pseudo sidebar brand content."
Assert-Contains $theme '#payrollApprovalContent' "hr-theme.css must keep the payroll approval content area visible."
Assert-Contains $theme '.payroll-approval-card' "hr-theme.css must keep the payroll approval card visible."

$payrollPage = Read-ProjectFile "src/main/webapp/Views/hr/PayrollManagement.jsp"
Assert-Contains $payrollPage 'id="payrollApprovalContent"' "PayrollManagement.jsp must expose the payroll approval content marker."
Assert-Contains $payrollPage 'class="card payroll-approval-card"' "PayrollManagement.jsp must wrap filters/table in a visible payroll approval card."
Assert-Contains $payrollPage 'filters-section' "PayrollManagement.jsp must render filters outside of the payrolls non-empty condition."
Assert-Contains $payrollPage 'table-container' "PayrollManagement.jsp must render the payroll table/empty state container."

$roleRedirectUtil = Read-ProjectFile "src/main/java/com/hrm/util/RoleRedirectUtil.java"
$homepageController = Read-ProjectFile "src/main/java/com/hrm/controller/HomepageController.java"
if ($roleRedirectUtil.Contains('/Views/hr/HrHome.jsp')) {
    throw "RoleRedirectUtil must route HR Manager to /HrHomeController instead of directly opening HrHome.jsp."
}
if ($homepageController.Contains('/Views/hr/HrHome.jsp')) {
    throw "HomepageController must route HR Manager dashboard links to /HrHomeController."
}

Write-Host "HR Manager shell UI checks passed."
