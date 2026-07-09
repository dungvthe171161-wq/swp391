<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Send Leave Application</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }
        h2 {
            color: #111;
        }
        form {
            max-width: 700px;
        }
        label {
            display: block;
            margin-top: 10px;
            font-weight: bold;
        }
        input, select, textarea {
            width: 100%;
            padding: 8px;
            margin-top: 4px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        button {
            margin-top: 15px;
            padding: 10px 18px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .primary { background-color: #007bff; color: white; }
        .danger { background-color: #dc3545; color: white; }
        .secondary { background-color: #6c757d; color: white; }
        .success { background-color: #28a745; color: white; }
        .link {
            display: inline-block;
            margin-top: 20px;
            text-decoration: none;
            color: #007bff;
        }
    </style>
    <script>
        function validateForm() {
            const start = document.getElementById('startDate').value;
            const end = document.getElementById('endDate').value;
            if (!start || !end) {
                alert("Please enter start and end date!");
                return false;
            }
            if (new Date(end) < new Date(start)) {
                alert("End date cannot be before start date!");
                return false;
            }
            return true;
        }

        function resetForm() {
            document.getElementById("leaveForm").reset();
        }

        function previewForm() {
            const name = document.getElementById("empName").value;
            const reason = document.getElementById("reason").value;
            alert("Preview Leave Application\n\nEmployee: " + name + "\nReason: " + reason);
        }
    </script>
</head>
<body>

<h2>Send Leave Application</h2>

<form id="leaveForm" action="leave_create" method="post" onsubmit="return validateForm()">

    <!-- Basic Fields -->
    <label>Employee ID:</label>
    <input type="text" name="empId" id="empId" placeholder="e.g. E001" required>

    <label>Employee Name:</label>
    <input type="text" name="empName" id="empName" required>

    <label>Department:</label>
    <select name="department">
        <option>IT</option>
        <option>HR</option>
        <option>Finance</option>
        <option>Sales</option>
        <option>Marketing</option>
    </select>

    <label>Position:</label>
    <input type="text" name="position" placeholder="e.g. Developer">

    <label>Leave Type:</label>
    <select name="leaveType">
        <option>Annual Leave</option>
        <option>Sick Leave</option>
<option>Unpaid Leave</option>
        <option>Personal Leave</option>
        <option>Maternity/Paternity Leave</option>
    </select>

    <label>Start Date:</label>
    <input type="date" id="startDate" name="startDate">

    <label>End Date:</label>
    <input type="date" id="endDate" name="endDate">

    <label>Start Time (Optional):</label>
    <input type="time" name="startTime">

    <label>End Time (Optional):</label>
    <input type="time" name="endTime">

    <label>Total Days:</label>
    <input type="number" name="totalDays" min="0" step="0.5" placeholder="e.g. 1.5">

    <label>Reason:</label>
    <textarea name="reason" id="reason" rows="4"></textarea>

    <label>Contact During Leave:</label>
    <input type="text" name="contactInfo" placeholder="e.g. 0909xxxxxx">

    <label>Emergency Contact:</label>
    <input type="text" name="emergencyContact" placeholder="Name - Phone">

    <label>Attachment (if any):</label>
    <input type="file" name="attachment">

    <label>Approver:</label>
    <select name="approver">
        <option>Team Leader</option>
        <option>Department Head</option>
        <option>HR Manager</option>
    </select>

    <label>Additional Comments:</label>
    <textarea name="comments" rows="3" placeholder="Any extra notes"></textarea>

    <!-- Action Buttons -->
    <button type="submit" class="primary">Submit Request</button>
    <button type="button" class="secondary" onclick="resetForm()">Reset Form</button>
    <button type="button" class="success" onclick="previewForm()">Preview</button>
    <button type="button" class="danger" onclick="alert('Draft saved locally!')">Save Draft</button>
</form>

<a href="Home.jsp" class="link">← Back To Dashboard</a>

</body>
</html>