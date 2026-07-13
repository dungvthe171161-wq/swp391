(function () {
    function createMessage(text, type) {
        const message = document.createElement('div');
        message.className = 'ai-chatbot__message ai-chatbot__message--' + type;
        message.textContent = text;
        return message;
    }

    function createFeedbackControls(messageId, submitFeedback) {
        const controls = document.createElement('div');
        controls.className = 'ai-chatbot__feedback';
        controls.setAttribute('aria-label', 'Đánh giá câu trả lời');
        [['Useful', 'thumb_up', 'Hữu ích'], ['NotUseful', 'thumb_down', 'Không hữu ích']]
            .forEach(function (item) {
                const button = document.createElement('button');
                button.type = 'button';
                button.className = 'ai-chatbot__feedback-button';
                button.dataset.rating = item[0];
                button.title = item[2];
                button.setAttribute('aria-label', item[2]);
                const icon = document.createElement('span');
                icon.className = 'material-symbols-outlined';
                icon.setAttribute('aria-hidden', 'true');
                icon.textContent = item[1];
                button.appendChild(icon);
                button.addEventListener('click', function () {
                    submitFeedback(messageId, item[0], controls);
                });
                controls.appendChild(button);
            });
        const status = document.createElement('span');
        status.className = 'ai-chatbot__feedback-status';
        status.setAttribute('aria-live', 'polite');
        controls.appendChild(status);
        return controls;
    }

    function createBotResponse(text, data, submitFeedback) {
        const group = document.createElement('div');
        group.className = 'ai-chatbot__message-group';
        group.appendChild(createMessage(text, 'bot'));
        if (data && data.messageId) {
            group.appendChild(createFeedbackControls(data.messageId, submitFeedback));
        }
        return group;
    }

    function scrollToBottom(container) {
        container.scrollTop = container.scrollHeight;
    }

    function renderSuggestions(container, suggestions, sendMessage) {
        container.innerHTML = '';
        (suggestions || []).forEach(function (suggestion) {
            const button = document.createElement('button');
            button.type = 'button';
            button.textContent = suggestion;
            button.addEventListener('click', function () {
                sendMessage(suggestion);
            });
            container.appendChild(button);
        });
    }

    function setupChatbot(root) {
        const endpoint = root.dataset.chatbotEndpoint;
        const feedbackEndpoint = root.dataset.chatbotFeedbackEndpoint;
        const toggle = root.querySelector('[data-chatbot-toggle]');
        const close = root.querySelector('[data-chatbot-close]');
        const panel = root.querySelector('[data-chatbot-panel]');
        const form = root.querySelector('[data-chatbot-form]');
        const input = root.querySelector('[data-chatbot-input]');
        const messages = root.querySelector('[data-chatbot-messages]');
        const suggestions = root.querySelector('[data-chatbot-suggestions]');
        const sendButton = root.querySelector('[data-chatbot-send]');
        const greetingMessage = root.querySelector('[data-chatbot-greeting]');
        let greetingLoaded = false;

        function refreshSendState() {
            sendButton.disabled = !input.value.trim();
        }

        async function fetchAnswer(text, eventType) {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({
                    message: text,
                    page: window.location.pathname,
                    eventType: eventType || 'message'
                })
            });
            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.reply || 'Không thể gửi câu hỏi.');
            }
            return data;
        }

        async function submitFeedback(messageId, rating, controls) {
            const buttons = controls.querySelectorAll('.ai-chatbot__feedback-button');
            const status = controls.querySelector('.ai-chatbot__feedback-status');
            buttons.forEach(function (button) { button.disabled = true; });
            status.textContent = 'Đang lưu...';
            try {
                const response = await fetch(feedbackEndpoint, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify({ messageId: messageId, rating: rating })
                });
                const data = await response.json();
                if (!response.ok) {
                    throw new Error(data.message || 'Không thể lưu phản hồi.');
                }
                buttons.forEach(function (button) {
                    button.classList.toggle('is-selected', button.dataset.rating === rating);
                });
                status.textContent = 'Cảm ơn phản hồi';
            } catch (error) {
                status.textContent = 'Chưa lưu được phản hồi';
            } finally {
                buttons.forEach(function (button) { button.disabled = false; });
            }
        }

        async function loadGreeting() {
            if (greetingLoaded) {
                return;
            }

            greetingLoaded = true;
            try {
                const data = await fetchAnswer('xin chao', 'greeting');
                if (data && data.reply) {
                    greetingMessage.textContent = data.reply;
                    renderSuggestions(suggestions, data.suggestions, sendMessage);
                }
            } catch (error) {
                renderSuggestions(suggestions, [
                    'C\u00e1ch n\u1ed9p h\u1ed3 s\u01a1',
                    'Xem tr\u1ea1ng th\u00e1i \u1ee9ng tuy\u1ec3n',
                    'Li\u00ean h\u1ec7 HR'
                ], sendMessage);
            }
        }

        function setOpen(open) {
            panel.hidden = !open;
            toggle.setAttribute('aria-expanded', String(open));
            if (open) {
                loadGreeting();
                input.focus();
                scrollToBottom(messages);
            }
        }

        async function sendMessage(rawMessage) {
            const text = (rawMessage || '').trim();
            if (!text) {
                refreshSendState();
                return;
            }

            messages.appendChild(createMessage(text, 'user'));
            input.value = '';
            sendButton.disabled = true;

            const loading = createMessage('\u0110ang tr\u1ea3 l\u1eddi...', 'bot ai-chatbot__message--loading');
            messages.appendChild(loading);
            scrollToBottom(messages);

            try {
                const data = await fetchAnswer(text);
                loading.remove();

                if (data && data.reply) {
                    messages.appendChild(createBotResponse(
                        data.reply,
                        data,
                        submitFeedback
                    ));
                    renderSuggestions(suggestions, data.suggestions, sendMessage);
                } else {
                    messages.appendChild(createMessage(
                        'Tôi chưa có thông tin phù hợp cho câu hỏi này. Bạn có thể chọn một nội dung gợi ý hoặc liên hệ HR để được hỗ trợ.',
                        'bot'));
                }
            } catch (error) {
                loading.remove();
                messages.appendChild(createMessage(
                    'Tôi chưa xử lý được yêu cầu lúc này, vui lòng thử lại hoặc liên hệ HR.',
                    'bot'));
            } finally {
                refreshSendState();
                scrollToBottom(messages);
            }
        }

        toggle.addEventListener('click', function () {
            setOpen(panel.hidden);
        });

        close.addEventListener('click', function () {
            setOpen(false);
        });

        input.addEventListener('input', refreshSendState);

        form.addEventListener('submit', function (event) {
            event.preventDefault();
            sendMessage(input.value);
        });

        root.querySelectorAll('[data-chatbot-suggestion]').forEach(function (button) {
            button.addEventListener('click', function () {
                sendMessage(button.dataset.chatbotSuggestion);
            });
        });

        refreshSendState();
    }

    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('[data-chatbot-root]').forEach(setupChatbot);
    });
})();