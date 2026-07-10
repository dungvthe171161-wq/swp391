(function () {
    "use strict";

    const exactMap = new Map([
        ["Home", "Trang chủ"],
        ["BetterHR Home", "BetterHR Trang chủ"],
        ["Dashboard", "Bảng điều khiển"],
        ["Profile", "Hồ sơ"],
        ["My Profile", "Hồ sơ của tôi"],
        ["Settings", "Cài đặt"],
        ["Logout", "Đăng xuất"],
        ["Login", "Đăng nhập"],
        ["Register", "Đăng ký"],
        ["Create Account", "Tạo tài khoản"],
        ["Forgot Password", "Quên mật khẩu"],
        ["Change Password", "Đổi mật khẩu"],
        ["Change Your Password", "Đổi mật khẩu"],
        ["Current Password", "Mật khẩu hiện tại"],
        ["New Password", "Mật khẩu mới"],
        ["Confirm Password", "Xác nhận mật khẩu"],
        ["Confirm New Password", "Xác nhận mật khẩu mới"],
        ["Save", "Lưu"],
        ["Save Changes", "Lưu thay đổi"],
        ["Save Department", "Lưu phòng ban"],
        ["Save Role", "Lưu vai trò"],
        ["Save Draft", "Lưu nháp"],
        ["Save State", "Lưu trạng thái"],
        ["Cancel", "Hủy"],
        ["Edit", "Sửa"],
        ["Edit Profile", "Chỉnh sửa hồ sơ"],
        ["Edit Department", "Sửa phòng ban"],
        ["Edit Role", "Sửa vai trò"],
        ["Delete", "Xóa"],
        ["Delete Selected", "Xóa mục đã chọn"],
        ["View", "Xem"],
        ["View Details", "Xem chi tiết"],
        ["Create", "Tạo mới"],
        ["Update", "Cập nhật"],
        ["Submit", "Gửi"],
        ["Submit Request", "Gửi yêu cầu"],
        ["Submit for Approval", "Gửi duyệt"],
        ["Back", "Quay lại"],
        ["Back to home", "Quay lại trang chủ"],
        ["Back to Login", "Quay lại đăng nhập"],
        ["Back to Admin", "Quay lại quản trị"],
        ["Back to Dashboard", "Quay lại bảng điều khiển"],
        ["Back To Dashboard", "Quay lại bảng điều khiển"],
        ["Back to previous page", "Quay lại trang trước"],
        ["Search", "Tìm kiếm"],
        ["Filter", "Lọc"],
        ["Apply Filter", "Áp dụng bộ lọc"],
        ["Clear Filter", "Xóa bộ lọc"],
        ["Status", "Trạng thái"],
        ["All Status", "Tất cả trạng thái"],
        ["Active", "Đang hoạt động"],
        ["Inactive", "Ngừng hoạt động"],
        ["Pending", "Chờ xử lý"],
        ["Approved", "Đã duyệt"],
        ["Rejected", "Từ chối"],
        ["Draft", "Bản nháp"],
        ["Success", "Thành công"],
        ["Error", "Lỗi"],
        ["Warning", "Cảnh báo"],
        ["Loading", "Đang tải"],
        ["No data", "Không có dữ liệu"],
        ["No recent activity", "Chưa có hoạt động gần đây"],
        ["No roles available", "Chưa có vai trò"],
        ["No detailed permissions found for this group.", "Không tìm thấy quyền chi tiết cho nhóm này."],
        ["Employee", "Nhân viên"],
        ["Employees", "Nhân viên"],
        ["Employee ID", "Mã nhân viên"],
        ["Employee Name", "Tên nhân viên"],
        ["Employee Profile", "Hồ sơ nhân viên"],
        ["Employee List", "Danh sách nhân viên"],
        ["Create Employee", "Tạo nhân viên"],
        ["Select Employee", "Chọn nhân viên"],
        ["Department", "Phòng ban"],
        ["Department ID", "Mã phòng ban"],
        ["Department Name", "Tên phòng ban"],
        ["Department Management", "Quản lý phòng ban"],
        ["Add New Department", "Thêm phòng ban mới"],
        ["Employee Count", "Số nhân viên"],
        ["Role", "Vai trò"],
        ["Role ID", "Mã vai trò"],
        ["Role Name", "Tên vai trò"],
        ["Role Management", "Quản lý vai trò"],
        ["Role Permissions", "Phân quyền vai trò"],
        ["Add New Role", "Thêm vai trò mới"],
        ["User", "Người dùng"],
        ["Users", "Người dùng"],
        ["User ID", "Mã người dùng"],
        ["User Name", "Tên người dùng"],
        ["Username", "Tên đăng nhập"],
        ["User Management", "Quản lý người dùng"],
        ["Add New User", "Thêm người dùng mới"],
        ["Full Name", "Họ và tên"],
        ["Phone", "Điện thoại"],
        ["Phone Number", "Số điện thoại"],
        ["Email", "Email"],
        ["Email Address", "Địa chỉ email"],
        ["Address", "Địa chỉ"],
        ["Description", "Mô tả"],
        ["Requirements", "Yêu cầu"],
        ["Recruitment", "Tuyển dụng"],
        ["Recruitment Management", "Quản lý tuyển dụng"],
        ["Application", "Hồ sơ ứng tuyển"],
        ["Job Application", "Ứng tuyển"],
        ["Apply Now", "Ứng tuyển ngay"],
        ["Contract", "Hợp đồng"],
        ["Contract Management", "Quản lý hợp đồng"],
        ["Create Contract", "Tạo hợp đồng"],
        ["Payroll", "Bảng lương"],
        ["Payroll Management", "Quản lý lương"],
        ["Payroll Details", "Chi tiết bảng lương"],
        ["Create Payroll", "Tạo bảng lương"],
        ["Create/Edit Payroll", "Tạo/Sửa bảng lương"],
        ["Attendance", "Chấm công"],
        ["Attendance Summary", "Tổng hợp chấm công"],
        ["Attendance Details", "Chi tiết chấm công"],
        ["Leave Application", "Đơn xin nghỉ"],
        ["Send Leave Application", "Gửi đơn xin nghỉ"],
        ["Reason", "Lý do"],
        ["Emergency Contact", "Liên hệ khẩn cấp"],
        ["Approver", "Người duyệt"],
        ["Start Date", "Ngày bắt đầu"],
        ["End Date", "Ngày kết thúc"],
        ["Created Date", "Ngày tạo"],
        ["Last Login", "Đăng nhập gần nhất"],
        ["Human Resources Management", "Quản lý nhân sự"],
        ["Human Resource Management System", "Hệ thống quản lý nhân sự"],
        ["All rights reserved.", "Đã đăng ký bản quyền."],
        ["HR Dashboard", "Bảng điều khiển HR"],
        ["Admin Profile", "Hồ sơ quản trị"],
        ["System Log", "Nhật ký hệ thống"],
        ["Audit Log", "Nhật ký kiểm tra"],
        ["Object", "Đối tượng"],
        ["Action", "Hành động"],
        ["Date", "Ngày"],
        ["Amount", "Số tiền"],
        ["Net Salary", "Lương thực nhận"],
        ["Base Salary", "Lương cơ bản"],
        ["Total Net Salary", "Tổng lương thực nhận"],
        ["Approval", "Phê duyệt"],
        ["Approve", "Duyệt"],
        ["Reject", "Từ chối"],
        ["Rejection Reason", "Lý do từ chối"],
        ["Manual Edit", "Chỉnh sửa thủ công"],
        ["Edit Values", "Sửa giá trị"],
        ["Select All", "Chọn tất cả"],
        ["Details", "Chi tiết"],
        ["Not provided", "Chưa cung cấp"],
        ["Not assigned", "Chưa phân công"],
        ["Not available", "Chưa có dữ liệu"],
        ["N/A", "Không có"]
    ]);

    const phraseRules = [
        [/\bUser Management\b/g, "Quản lý người dùng"],
        [/\bDepartment Management\b/g, "Quản lý phòng ban"],
        [/\bRole Management\b/g, "Quản lý vai trò"],
        [/\bRole Permissions\b/g, "Phân quyền vai trò"],
        [/\bEmployee List\b/g, "Danh sách nhân viên"],
        [/\bEmployee Profile\b/g, "Hồ sơ nhân viên"],
        [/\bCreate Employee\b/g, "Tạo nhân viên"],
        [/\bPayroll Management\b/g, "Quản lý lương"],
        [/\bContract Management\b/g, "Quản lý hợp đồng"],
        [/\bRecruitment Management\b/g, "Quản lý tuyển dụng"],
        [/\bHuman Resources Management\b/g, "Quản lý nhân sự"],
        [/\bHuman Resource Management System\b/g, "Hệ thống quản lý nhân sự"],
        [/\bAll rights reserved\./g, "Đã đăng ký bản quyền."],
        [/\bSearch username\.\.\./g, "Tìm tên đăng nhập..."],
        [/\bDepartment Name or ID\.\.\./g, "Tên hoặc mã phòng ban..."],
        [/\bRole Name or ID\.\.\./g, "Tên hoặc mã vai trò..."],
        [/\bLogID, User, Action, Object\.\.\./g, "Mã log, người dùng, hành động, đối tượng..."],
        [/\bNo departments found matching the criteria\./g, "Không tìm thấy phòng ban phù hợp."],
        [/\bNo roles found matching the criteria\./g, "Không tìm thấy vai trò phù hợp."],
        [/\bNo system log records found matching the criteria\./g, "Không tìm thấy nhật ký hệ thống phù hợp."],
        [/\bNo payroll found\. Click "Create Payroll" to create one\./g, "Chưa có bảng lương. Nhấn \"Tạo bảng lương\" để tạo mới."],
        [/\bStart using the system to see your activity here\b/g, "Hãy sử dụng hệ thống để xem hoạt động tại đây"],
        [/\bEnter your current and new password\b/g, "Nhập mật khẩu hiện tại và mật khẩu mới"],
        [/\bEnter your new password below\b/g, "Nhập mật khẩu mới bên dưới"],
        [/\bPlease select Employee and Pay Period first\./g, "Vui lòng chọn nhân viên và kỳ lương trước."],
        [/\bPlease select Employee and Pay Period\./g, "Vui lòng chọn nhân viên và kỳ lương."],
        [/\bYou are manually editing calculated values\./g, "Bạn đang chỉnh sửa thủ công các giá trị đã tính."],
        [/\bAttendance data is automatically calculated and applied to payroll\b/g, "Dữ liệu chấm công được tự động tính và áp dụng vào bảng lương"],
        [/\bLoading payroll details\.\.\./g, "Đang tải chi tiết bảng lương..."],
        [/\bError loading payroll data\b/g, "Lỗi khi tải dữ liệu bảng lương"],
        [/\bError loading attendance data\b/g, "Lỗi khi tải dữ liệu chấm công"],
        [/\bNetwork error\b/g, "Lỗi mạng"],
        [/\bUpdate failed\./g, "Cập nhật thất bại."],
        [/\bRole deleted successfully!/g, "Xóa vai trò thành công!"],
        [/\bDepartment Name is required\./g, "Tên phòng ban là bắt buộc."],
        [/\bRole name is required\./g, "Tên vai trò là bắt buộc."]
    ];

    const wordMap = new Map([
        ["Dashboard", "Bảng điều khiển"],
        ["Profile", "Hồ sơ"],
        ["Logout", "Đăng xuất"],
        ["Search", "Tìm kiếm"],
        ["Filter", "Lọc"],
        ["View", "Xem"],
        ["Edit", "Sửa"],
        ["Delete", "Xóa"],
        ["Save", "Lưu"],
        ["Cancel", "Hủy"],
        ["Create", "Tạo"],
        ["Update", "Cập nhật"],
        ["Submit", "Gửi"],
        ["Employee", "Nhân viên"],
        ["Department", "Phòng ban"],
        ["Role", "Vai trò"],
        ["User", "Người dùng"],
        ["Username", "Tên đăng nhập"],
        ["Status", "Trạng thái"],
        ["Active", "Đang hoạt động"],
        ["Inactive", "Ngừng hoạt động"],
        ["Pending", "Chờ xử lý"],
        ["Approved", "Đã duyệt"],
        ["Rejected", "Từ chối"],
        ["Draft", "Bản nháp"],
        ["Phone", "Điện thoại"],
        ["Address", "Địa chỉ"],
        ["Password", "Mật khẩu"],
        ["Contract", "Hợp đồng"],
        ["Payroll", "Bảng lương"],
        ["Attendance", "Chấm công"],
        ["Management", "Quản lý"],
        ["Details", "Chi tiết"],
        ["Warning", "Cảnh báo"],
        ["Error", "Lỗi"],
        ["Success", "Thành công"],
        ["Loading", "Đang tải"],
        ["Back", "Quay lại"],
        ["Approval", "Phê duyệt"]
    ]);

    const skipTags = new Set(["SCRIPT", "STYLE", "NOSCRIPT", "CODE", "PRE", "TEXTAREA"]);
    const brandToken = "__BETTER_HR_BRAND__";

    function preserveBrand(text) {
        return text.replace(/Better\s*HR|BetterHR/gi, brandToken);
    }

    function restoreBrand(text) {
        return text.replaceAll(brandToken, "BetterHR");
    }

    function translateText(input) {
        if (!input || !input.trim()) return input;

        const leading = input.match(/^\s*/)[0];
        const trailing = input.match(/\s*$/)[0];
        let text = input.trim();
        let protectedText = preserveBrand(text);

        if (exactMap.has(protectedText)) {
            return leading + restoreBrand(exactMap.get(protectedText)) + trailing;
        }

        phraseRules.forEach(([pattern, replacement]) => {
            protectedText = protectedText.replace(pattern, replacement);
        });

        wordMap.forEach((replacement, source) => {
            protectedText = protectedText.replace(new RegExp("\\b" + source + "\\b", "g"), replacement);
        });

        return leading + restoreBrand(protectedText) + trailing;
    }

    function translateAttributes(element) {
        ["placeholder", "title", "alt", "aria-label"].forEach(attr => {
            if (element.hasAttribute && element.hasAttribute(attr)) {
                const current = element.getAttribute(attr);
                const translated = translateText(current);
                if (translated !== current) element.setAttribute(attr, translated);
            }
        });

        if ((element.tagName === "INPUT" || element.tagName === "BUTTON") && element.hasAttribute("value")) {
            const type = (element.getAttribute("type") || "").toLowerCase();
            if (["button", "submit", "reset"].includes(type)) {
                const current = element.getAttribute("value");
                const translated = translateText(current);
                if (translated !== current) element.setAttribute("value", translated);
            }
        }
    }

    function translateNode(root) {
        if (!root || skipTags.has(root.nodeName)) return;

        if (root.nodeType === Node.ELEMENT_NODE) {
            translateAttributes(root);
        }

        const walker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT, {
            acceptNode(node) {
                const parent = node.parentElement;
                if (!parent || skipTags.has(parent.tagName)) return NodeFilter.FILTER_REJECT;
                if (!node.nodeValue || !node.nodeValue.trim()) return NodeFilter.FILTER_REJECT;
                return NodeFilter.FILTER_ACCEPT;
            }
        });

        const textNodes = [];
        while (walker.nextNode()) textNodes.push(walker.currentNode);

        textNodes.forEach(node => {
            const translated = translateText(node.nodeValue);
            if (translated !== node.nodeValue) node.nodeValue = translated;
        });

        if (root.querySelectorAll) {
            root.querySelectorAll("*").forEach(translateAttributes);
        }
    }

    function run() {
        document.title = translateText(document.title);
        translateNode(document.body);
        const observer = new MutationObserver(mutations => {
            mutations.forEach(mutation => {
                mutation.addedNodes.forEach(node => {
                    if (node.nodeType === Node.ELEMENT_NODE || node.nodeType === Node.TEXT_NODE) {
                        translateNode(node.nodeType === Node.TEXT_NODE ? node.parentElement : node);
                    }
                });
                if (mutation.type === "characterData") {
                    const translated = translateText(mutation.target.nodeValue);
                    if (translated !== mutation.target.nodeValue) {
                        mutation.target.nodeValue = translated;
                    }
                }
            });
        });
        observer.observe(document.body, { childList: true, subtree: true, characterData: true });
    }

    const nativeAlert = window.alert;
    const nativeConfirm = window.confirm;

    window.alert = function (message) {
        return nativeAlert.call(window, typeof message === "string" ? translateText(message) : message);
    };

    window.confirm = function (message) {
        return nativeConfirm.call(window, typeof message === "string" ? translateText(message) : message);
    };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", run);
    } else {
        run();
    }
})();
