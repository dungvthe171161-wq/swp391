// Function to handle form submission
document.getElementById('filterForm').addEventListener('submit', function(e) {
    // Form will be submitted normally, no need to prevent default
    // The server-side will handle the filtering
});

// Function to update filter counts
function updateFilterCounts() {
    const departmentSelect = document.getElementById('departmentFilter');
    const statusSelect = document.getElementById('statusFilter');
    const genderSelect = document.getElementById('genderFilter');
    const positionSelect = document.getElementById('positionFilter');

    // Update department filter counts
    Array.from(departmentSelect.options).forEach(option => {
        if (option.getAttribute('data-count')) {
            const count = option.getAttribute('data-count');
            option.textContent = `${option.getAttribute('data-name')} (${count})`;
        }
    });

    // Update status filter counts
    Array.from(statusSelect.options).forEach(option => {
        if (option.getAttribute('data-count')) {
            const count = option.getAttribute('data-count');
            option.textContent = `${option.getAttribute('data-name')} (${count})`;
        }
    });

    // No need to update counts for gender as they are static options

    // Update position filter counts
    Array.from(positionSelect.options).forEach(option => {
        if (option.getAttribute('data-count')) {
            const count = option.getAttribute('data-count');
            option.textContent = `${option.getAttribute('data-name')} (${count})`;
        }
    });
}

// Function to clear all filters
function clearFilters() {
    const form = document.getElementById('filterForm');
    const searchInput = form.querySelector('input[name="search"]');
    const selects = form.querySelectorAll('select');

    // Clear search input
    searchInput.value = '';

    // Reset all select elements to their first option
    selects.forEach(select => {
        select.selectedIndex = 0;
    });

    // Submit the form to refresh the results
    form.submit();
}

// Add clear functionality to the clear button
document.querySelector('.btn-clear').addEventListener('click', function(e) {
    e.preventDefault();
    clearFilters();
});

// Initialize filter counts when page loads
document.addEventListener('DOMContentLoaded', function() {
    updateFilterCounts();
});