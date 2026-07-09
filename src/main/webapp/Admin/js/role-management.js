
let currentPage = 1
const currentSearch = ""
const deleteRoleId = null
const pageSize = 10
const $ = window.$ // Declare the $ variable
const contextPath = window.contextPath // Declare the contextPath variable

// Load roles on page load
$(document).ready(function() {
    // Initialize user menu dropdown
    $('.user-menu-trigger').click(function(e) {
        e.stopPropagation();
        $(this).siblings('.user-menu-dropdown').toggleClass('show');
    });

    // Close dropdown when clicking outside
    $(document).click(function() {
        $('.user-menu-dropdown').removeClass('show');
    });

    loadRoles();
});


function loadRoles(page = 1) {
  currentPage = page
  const search = $("#roleNameFilter").val() || ""

  console.log("[v0] loadRoles called - page:", page, "search:", search)

  const params = new URLSearchParams({
    page: page,
    pageSize: pageSize,
    search: search,
  })

  const url = contextPath + "/admin/role?" + params.toString()

  console.log("[v0] Fetching from URL:", url)

  $.get(url)
    .done((data) => {
      console.log("[v0] Response received:", data)
      displayRoles(data)
      displayPagination(data)
    })
    .fail((xhr) => {
      console.log("[v0] Lỗi:", xhr)
      alert("Không thể tải vai trò: " + (xhr.responseJSON?.message || "Lỗi không xác định"))
    })

}

// Display roles in table
function displayRoles(data) {
    const tbody = $('#rolesTableBody');
    tbody.empty();


  if (data.roles && data.roles.length > 0) {
    data.roles.forEach((role) => {
      tbody.append(`

                <tr>
                    <td>${role.roleId}</td>
                    <td>${role.roleName}</td>
                    <td>
                        <div class="action-buttons">
                            <button class="btn-edit" onclick="editRole(${role.roleId})">Sửa</button>
                            <button class="btn-delete" onclick="deleteRole(${role.roleId})">Xóa</button>
                        </div>
                    </td>
                </tr>
            `);
        });
    } else {
        tbody.append(`
            <tr>
                <td colspan="3" style="text-align: center; padding: 20px; color: #9ca3af;">
                    Không tìm thấy vai trò
                </td>
            </tr>
        `);
    }
}

// Display pagination
function displayPagination(data) {
    const pagination = $('.pagination-bar');
    pagination.empty();


  const totalItems = data.totalItems || 0
  const totalPages = data.totalPages || 1
  const start = (currentPage - 1) * pageSize + 1
  const end = Math.min(currentPage * pageSize, totalItems)

  console.log("[v0] Pagination - total:", totalItems, "pages:", totalPages, "current:", currentPage)

  // Add info text
  const infoDiv = $(`
        <div class="pagination-info">
            Hiển thị ${totalItems === 0 ? 0 : start} - ${end} / ${totalItems}
        </div>
    `)
  pagination.append(infoDiv)

  // Add controls
  const controlsDiv = $('<div class="pagination-controls"></div>')

  if (totalPages > 1) {
    // Previous button
    if (currentPage > 1) {
      controlsDiv.append(`<a onclick="loadRoles(1)">Đầu</a>`)
      controlsDiv.append(`<a onclick="loadRoles(${currentPage - 1})">Trước</a>`)
    } else {
      controlsDiv.append('<span class="disabled">Đầu</span>')
      controlsDiv.append('<span class="disabled">Trước</span>')
    }

    // Page numbers
    const startPage = Math.max(1, currentPage - 2)
    const endPage = Math.min(totalPages, currentPage + 2)

    if (startPage > 1) {
      controlsDiv.append(`<a onclick="loadRoles(1)">1</a>`)
      if (startPage > 2) {
        controlsDiv.append('<span class="ellipsis">...</span>')
      }
    }

    for (let i = startPage; i <= endPage; i++) {
      if (i === currentPage) {
        controlsDiv.append(`<span class="active">${i}</span>`)
      } else {
        controlsDiv.append(`<a onclick="loadRoles(${i})">${i}</a>`)
      }
    }

    if (endPage < totalPages) {
      if (endPage < totalPages - 1) {
        controlsDiv.append('<span class="ellipsis">...</span>')
      }
      controlsDiv.append(`<a onclick="loadRoles(${totalPages})">${totalPages}</a>`)
    }

    // Sau button
    if (currentPage < totalPages) {
      controlsDiv.append(`<a onclick="loadRoles(${currentPage + 1})">Sau</a>`)
      controlsDiv.append(`<a onclick="loadRoles(${totalPages})">Cuối</a>`)
    } else {
      controlsDiv.append('<span class="disabled">Sau</span>')
      controlsDiv.append('<span class="disabled">Cuối</span>')
    }
  }

  pagination.append(controlsDiv)
}

function openAddRoleModal() {
    document.getElementById('modalTitle').textContent = 'Thêm vai trò mới';
    document.getElementById('roleForm').reset();
    document.getElementById('roleModal').classList.add('show');
}

function closeRoleModal() {
    document.getElementById('roleModal').classList.remove('show');
}

function editRole(roleId) {
    // Fetch role data and populate form
    fetch(contextPath + '/admin/role/' + roleId)
        .then(response => response.json())
        .then(data => {
            document.getElementById('modalTitle').textContent = 'Sửa vai trò';
            document.getElementById('roleName').value = data.roleName;
            document.getElementById('roleModal').classList.add('show');
        });
}

// Save or update role
$(document).on('submit', '#roleForm', function(e) {
    e.preventDefault();
    const roleName = document.getElementById('roleName').value;
    const isEdit = document.getElementById('modalTitle').textContent === 'Sửa vai trò';
    
    const method = isEdit ? 'PUT' : 'POST';
    const url = isEdit ? 
        contextPath + '/admin/role/' + roleId :
        contextPath + '/admin/role';

    $.ajax({
        url: url,
        method: method,
        contentType: 'application/json',
        data: JSON.stringify({ roleName: roleName })
    })
        .done(function() {
            closeRoleModal();
            loadRoles(currentPage);
        })
        .fail(function(xhr) {
            alert('Không thể lưu vai trò: ' + xhr.responseJSON?.message || 'Lỗi không xác định');
        });
});

function deleteRole(roleId) {
    if (confirm('Bạn có chắc muốn xóa vai trò này không? Thao tác này không thể hoàn tác nếu vai trò không còn được sử dụng.')) {
        $.ajax({
            url: contextPath + '/admin/role/' + roleId,
            method: 'DELETE'
        })
            .done(function() {
                loadRoles(currentPage);
            })
            .fail(function(xhr) {
                if (xhr.status === 409) {
                    alert('Không thể xóa vai trò này vì đang được gán cho người dùng.');
                } else {
                    alert('Không thể xóa vai trò: ' + xhr.responseJSON?.message || 'Lỗi không xác định');
                }
            });
    }
}

// Filter roles on input change
$('#roleNameFilter').on('keyup', function() {
    loadRoles(1);
});

// Close modal on outside click
$(window).on('click', function(event) {
    if ($(event.target).is('#roleModal')) {
        closeRoleModal();
    }
});
