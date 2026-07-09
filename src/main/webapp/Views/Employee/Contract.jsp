<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - H&#7907;p &#273;&#7891;ng</title>
    <%@ include file="_EmployeeStyles.jspf" %>
    <style>
        .contract-sign-box {
            margin-top: 22px;
            padding: 16px;
            border: 1px solid #9dddbb;
            border-radius: 12px;
            background: #effaf4;
        }
        .contract-sign-box p {
            margin: 0 0 14px;
            color: var(--bh-primary-dark);
            font-weight: 700;
        }
        .contract-sign-box.waiting {
            border-color: var(--bh-border);
            background: #f7f4ee;
        }
        .contract-sign-box.waiting p {
            color: var(--bh-muted);
        }
        .disabled-sign-button {
            min-height: 42px;
            border: 1px solid var(--bh-border);
            border-radius: 12px;
            padding: 0 16px;
            background: #e8e1d7;
            color: var(--bh-muted);
            font-weight: 800;
            cursor: not-allowed;
        }
        .status-pill {
            display: inline-flex;
            align-items: center;
            min-height: 30px;
            padding: 0 10px;
            border-radius: 999px;
            background: #e8f1ff;
            color: #1e40af;
            font-size: 13px;
            font-weight: 800;
        }
        .status-pill.active {
            background: #e3f5eb;
            color: var(--bh-primary-dark);
        }
        .status-pill.rejected {
            background: var(--bh-danger-soft);
            color: var(--bh-danger);
        }
        .contract-document {
            margin-top: 22px;
            padding: 18px;
            border: 1px solid var(--bh-border);
            border-radius: 12px;
            background: #fffdf8;
        }
        .contract-document h2 {
            margin: 0 0 12px;
            color: var(--bh-primary-dark);
            font-size: 20px;
        }
        .contract-document pre {
            margin: 0;
            white-space: pre-wrap;
            word-break: break-word;
            color: var(--bh-text);
            font-family: inherit;
            line-height: 1.6;
        }
        .document-file-link {
            display: inline-flex;
            align-items: center;
            min-height: 40px;
            margin-top: 14px;
            padding: 0 14px;
            border-radius: 10px;
            background: var(--bh-primary);
            color: #fff;
            text-decoration: none;
            font-weight: 800;
        }
        .contract-document.missing {
            border-color: var(--bh-danger-soft);
            background: #fff7f7;
            color: var(--bh-danger);
            font-weight: 700;
        }
        .sign-confirm {
            display: flex;
            gap: 10px;
            align-items: flex-start;
            margin: 0 0 14px;
            color: var(--bh-primary-dark);
            font-weight: 700;
        }
        .sign-confirm input {
            margin-top: 4px;
        }
    </style>
</head>
<body>
<div class="employee-shell">
    <%@ include file="_EmployeeSidebar.jspf" %>
    <main class="employee-main">
        <%@ include file="_EmployeeTopbar.jspf" %>
        <div class="content">
            <h1 class="page-title">H&#7907;p &#273;&#7891;ng</h1>
            <p class="page-note">Theo d&#245;i h&#7907;p &#273;&#7891;ng m&#7899;i nh&#7845;t v&#224; k&#253; x&#225;c nh&#7853;n khi h&#7907;p &#273;&#7891;ng &#273;&#227; &#273;&#432;&#7907;c HR duy&#7879;t.</p>

            <c:if test="${not empty employeeSuccess}">
                <div class="alert success">${employeeSuccess}</div>
            </c:if>
            <c:if test="${not empty employeeError}">
                <div class="alert error">${employeeError}</div>
            </c:if>

            <section class="panel">
                <div class="panel-inner">
                    <c:choose>
                        <c:when test="${not empty contract}">
                            <div class="stat-row">
                                <div class="stat"><span>M&#227; h&#7907;p &#273;&#7891;ng</span><strong>${contract.contractId}</strong></div>
                                <div class="stat"><span>Ng&#224;y b&#7855;t &#273;&#7847;u</span><strong>${contract.startDate}</strong></div>
                                <div class="stat"><span>Ng&#224;y k&#7871;t th&#250;c</span><strong>${contract.endDate}</strong></div>
                                <div class="stat"><span>L&#432;&#417;ng c&#417; b&#7843;n</span><strong>${contract.baseSalary}</strong></div>
                                <div class="stat"><span>Ph&#7909; c&#7845;p</span><strong>${contract.allowance}</strong></div>
                                <div class="stat"><span>Lo&#7841;i h&#7907;p &#273;&#7891;ng</span><strong>${contract.contractType}</strong></div>
                                <div class="stat">
                                    <span>Tr&#7841;ng th&#225;i</span>
                                    <strong>
                                        <c:choose>
                                            <c:when test="${contract.status eq 'Pending_Approval'}">
                                                <span class="status-pill">Ch&#7901; HR duy&#7879;t</span>
                                            </c:when>
                                            <c:when test="${contract.status eq 'Pending_Signature'}">
                                                <span class="status-pill">Ch&#7901; nh&#226;n vi&#234;n k&#253;</span>
                                            </c:when>
                                            <c:when test="${contract.status eq 'Active'}">
                                                <span class="status-pill active">&#272;ang hi&#7879;u l&#7921;c</span>
                                            </c:when>
                                            <c:when test="${contract.status eq 'Rejected'}">
                                                <span class="status-pill rejected">B&#7883; t&#7915; ch&#7889;i</span>
                                            </c:when>
                                            <c:otherwise>${contract.status}</c:otherwise>
                                        </c:choose>
                                    </strong>
                                </div>
                                <div class="stat"><span>Th&#7901;i gian k&#253;</span><strong>${empty contract.signedAt ? 'Ch&#432;a k&#253;' : contract.signedAt}</strong></div>
                                <div class="stat"><span>Ghi ch&#250;</span><strong>${contract.note}</strong></div>
                            </div>

                            <c:if test="${not empty contractDocument}">
                                <div class="contract-document">
                                    <h2><c:out value="${contractDocument.title}"/></h2>
                                    <pre><c:out value="${contractDocument.content}"/></pre>
                                    <c:if test="${not empty contractDocument.fileName}">
                                        <a class="document-file-link"
                                           href="${pageContext.request.contextPath}/employee/contract/document?contractId=${contract.contractId}"
                                           target="_blank" rel="noopener">
                                            M&#7903; t&#7879;p: <c:out value="${contractDocument.fileName}"/>
                                        </a>
                                    </c:if>
                                </div>
                            </c:if>
                            <c:if test="${empty contractDocument and (contract.status eq 'Pending_Signature' or contract.status eq 'Active')}">
                                <div class="contract-document missing">
                                    H&#7907;p &#273;&#7891;ng n&#224;y ch&#432;a c&#243; v&#259;n b&#7843;n &#273;&#237;nh k&#232;m. Vui l&#242;ng li&#234;n h&#7879; HR Staff &#273;&#7875; b&#7893; sung.
                                </div>
                            </c:if>

                            <c:choose>
                                <c:when test="${contract.status eq 'Pending_Signature'}">
                                    <div class="contract-sign-box">
                                        <p>H&#7907;p &#273;&#7891;ng n&#224;y &#273;&#227; &#273;&#432;&#7907;c HR duy&#7879;t. B&#7841;n c&#7847;n k&#253; x&#225;c nh&#7853;n &#273;&#7875; h&#7907;p &#273;&#7891;ng chuy&#7875;n sang tr&#7841;ng th&#225;i &#273;ang hi&#7879;u l&#7921;c.</p>
                                        <c:choose>
                                            <c:when test="${not empty contractDocument}">
                                                <form method="post" action="${pageContext.request.contextPath}/employee/contract">
                                                    <input type="hidden" name="contractId" value="${contract.contractId}">
                                                    <label class="sign-confirm" for="agreeDocument">
                                                        <input type="checkbox" id="agreeDocument" name="agreeDocument" value="1" required>
                                                        <span>T&#244;i &#273;&#227; &#273;&#7885;c v&#224; &#273;&#7891;ng &#253; v&#7899;i v&#259;n b&#7843;n h&#7907;p &#273;&#7891;ng n&#224;y.</span>
                                                    </label>
                                                    <button class="primary-button" type="submit" onclick="return confirm('B&#7841;n x&#225;c nh&#7853;n k&#253; v&#224; ch&#7845;p nh&#7853;n h&#7907;p &#273;&#7891;ng n&#224;y?')">
                                                        K&#253; v&#224; ch&#7845;p nh&#7853;n
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <button class="disabled-sign-button" type="button" disabled>Ch&#432;a c&#243; v&#259;n b&#7843;n</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:when>
                                <c:when test="${contract.status eq 'Pending_Approval'}">
                                    <div class="contract-sign-box waiting">
                                        <p>H&#7907;p &#273;&#7891;ng &#273;ang ch&#7901; HR Manager duy&#7879;t. Sau khi HR duy&#7879;t, n&#250;t k&#253; s&#7869; m&#7903; ngay t&#7841;i &#273;&#226;y.</p>
                                        <button class="disabled-sign-button" type="button" disabled>Ch&#7901; HR duy&#7879;t</button>
                                    </div>
                                </c:when>
                                <c:when test="${contract.status eq 'Active'}">
                                    <div class="contract-sign-box">
                                        <p>H&#7907;p &#273;&#7891;ng &#273;&#227; &#273;&#432;&#7907;c k&#253; v&#224; &#273;ang c&#243; hi&#7879;u l&#7921;c.</p>
                                    </div>
                                </c:when>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <p>Ch&#432;a c&#243; h&#7907;p &#273;&#7891;ng cho nh&#226;n vi&#234;n n&#224;y.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
