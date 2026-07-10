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

function Assert-NotContains {
    param(
        [string] $Content,
        [string] $Needle,
        [string] $Message
    )
    if ($Content.Contains($Needle)) {
        throw $Message
    }
}

$hrStaffPages = @(
    "src/main/webapp/Views/HrStaff/HrStaffHome.jsp",
    "src/main/webapp/Views/HrStaff/PayrollManagement.jsp",
    "src/main/webapp/Views/HrStaff/CreateContract.jsp",
    "src/main/webapp/Views/HrStaff/ContractList.jsp",
    "src/main/webapp/Views/HrStaff/PostRecruitment.jsp",
    "src/main/webapp/Views/HrStaff/ViewCandidate.jsp",
    "src/main/webapp/Views/HrStaff/CreateNewRecruitment.jsp"
)

foreach ($page in $hrStaffPages) {
    $content = Read-ProjectFile $page
    Assert-Contains $content '<link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hr-staff-shell-20260627-1">' "$page must load the cache-busted shared HR Staff theme stylesheet."
    Assert-Contains $content '<%@ include file="_HrStaffSidebar.jspf" %>' "$page must statically include the shared HR Staff dashboard sidebar."
    Assert-Contains $content '<%@ include file="_HrStaffTopbar.jspf" %>' "$page must statically include the shared HR Staff dashboard topbar."
    Assert-Contains $content 'hrStaffSidebarActive' "$page must set hrStaffSidebarActive before including the shared sidebar."
    Assert-Contains $content 'staff-shell' "$page must use the dashboard staff-shell wrapper."
    Assert-Contains $content 'staff-main' "$page must use the dashboard staff-main wrapper."
}

$sidebar = Read-ProjectFile "src/main/webapp/Views/HrStaff/_HrStaffSidebar.jspf"
Assert-Contains $sidebar 'pageEncoding="UTF-8"' "Shared HR Staff sidebar fragment must declare UTF-8 pageEncoding."
if ($sidebar.Contains('contentType=')) {
    throw "Shared HR Staff sidebar fragment must not declare contentType; parent JSPs own response content type."
}
$expectedSidebarSnippets = @(
    'staff-sidebar',
    'sidebar-section',
    'brand-mark',
    'fa-table-cells-large',
    'fa-file-signature',
    'fa-money-check-dollar',
    'fa-file-circle-plus',
    'fa-folder-open',
    'fa-bullhorn',
    'fa-users-viewfinder',
    'BetterHR',
    '/hrstaff',
    '/hrstaff/payroll',
    '/hrstaff/contracts/create',
    '/hrstaff/contracts',
    '/postRecruitments',
    '/candidates'
)
foreach ($snippet in $expectedSidebarSnippets) {
    Assert-Contains $sidebar $snippet "Shared HR Staff sidebar must contain snippet '$snippet'."
}

$topbar = Read-ProjectFile "src/main/webapp/Views/HrStaff/_HrStaffTopbar.jspf"
Assert-Contains $topbar 'pageEncoding="UTF-8"' "Shared HR Staff topbar fragment must declare UTF-8 pageEncoding."
if ($topbar.Contains('contentType=')) {
    throw "Shared HR Staff topbar fragment must not declare contentType; parent JSPs own response content type."
}
$expectedTopbarSnippets = @(
    'topbar',
    'topbar-title',
    'topbar-icon',
    'search-box',
    'notification-dot',
    'avatar-chip',
    'home-button',
    'fa-briefcase',
    'fa-house'
)
foreach ($snippet in $expectedTopbarSnippets) {
    Assert-Contains $topbar $snippet "Shared HR Staff topbar must contain snippet '$snippet'."
}

$theme = Read-ProjectFile "src/main/webapp/css/hr-theme.css"
Assert-Contains $theme '.staff-shell' "hr-theme.css must define the shared HR Staff dashboard shell."
Assert-Contains $theme '.staff-sidebar' "hr-theme.css must style the shared HR Staff sidebar."
Assert-Contains $theme '.staff-main' "hr-theme.css must style the shared HR Staff main area."
Assert-Contains $theme '.staff-content' "hr-theme.css must normalize HR Staff child page content."
Assert-Contains $theme '#00482f' "hr-theme.css must keep the BetterHR dark green token."
Assert-Contains $theme '#97f6c0' "hr-theme.css must keep the BetterHR mint active token."

$postRecruitmentPage = Read-ProjectFile "src/main/webapp/Views/HrStaff/PostRecruitment.jsp"
Assert-Contains $postRecruitmentPage 'hr-staff-create-recruitment-action' "PostRecruitment.jsp must show the create recruitment action inside visible page content, not only inside the hidden legacy topbar."
Assert-Contains $postRecruitmentPage '${pageContext.request.contextPath}/detailRecruitmentCreate' "PostRecruitment.jsp must link to the create recruitment form."
Assert-Contains $postRecruitmentPage 'hr-staff-recruitment-page-title' "PostRecruitment.jsp must expose a visible Vietnamese page title marker."
$removedRecruitmentFilterSnippets = @(
    'searchByTitle',
    'filterStatus',
    'startDate',
    'endDate',
    'Apply Filter',
    'Search by Title',
    'Clear'
)
foreach ($snippet in $removedRecruitmentFilterSnippets) {
    Assert-NotContains $postRecruitmentPage $snippet "PostRecruitment.jsp must remove the recruitment filter UI and stale query parameters: '$snippet'."
}
$englishRecruitmentSnippets = @(
    'Recruitment Management',
    'Create New',
    'Create New Post',
    'No recruitment posts match',
    'Posted on:',
    'Applicants:',
    'View</a>',
    'Send</a>',
    'Delete</a>',
    'Are you sure'
)
foreach ($snippet in $englishRecruitmentSnippets) {
    Assert-NotContains $postRecruitmentPage $snippet "PostRecruitment.jsp visible copy must be Vietnamese; found stale English snippet '$snippet'."
}

Write-Host "HR Staff shell UI checks passed."
