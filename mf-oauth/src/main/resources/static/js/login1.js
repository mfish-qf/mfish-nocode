const {createApp} = Vue;
const app = createApp({
    data() {
        return {
            userPasswordVisible: false,
            phoneSmsCodeVisible: false,
            qrCodeVisible: false,
            captchaValue: '',
            captchaKey: '',
            captchaUrl: '',
            phone: '',
            username: 'admin',
            password: '!QAZ2wsx',
            loginType: '',
            errorVisible: false,
            errorMsg: '',
            rememberMe: false,
            codeValue: '',
            codeButton: '验证码',
            codeDisabled: false,
            qrCode: '',
            timer: '',
            qrCodeDesc: '打开小程序扫码',
            qrCodeName: '',
            qrCodeSecret: '',
            allowScan: true
        }
    },
    mounted() {
        this.initLoginData();
        this.getCaptcha();
        this.showError();
    },
    methods: {
        login() {
            $('#login').submit();
        },
        smsLogin() {
            $('#smsLogin').submit();
        },
        sendMsg() {
            $.ajax({
                url: "sendMsg",
                type: "POST",
                data: "phone=" + this.phone,
                dataType: "json",
                success: function (result) {
                    if (200 == result.code) {
                        //测试环境将验证码返回，生成环境删除此方法
                        app.codeValue = result.data;
                        app.resetCode();
                    } else {
                        app.showError(result.data.msg);
                    }
                }
            });
        },
        getCaptcha() {
            $.ajax({
                url: "/captcha",
                type: "get",
                success: function (result) {
                    if (200 == result.code) {
                        app.captchaUrl = "data:image/gif;base64," + result.data.img;
                        app.captchaKey = result.data.captchaKey;
                    } else {
                        app.showError(result.data.msg);
                    }
                }
            });
        },
        buildQRCode() {
            $.ajax({
                url: "qrCodeLogin/build",
                type: "get",
                success: function (result) {
                    if (result != null && 200 == result.code) {
                        app.qrCode = result.data.img;
                        app.allowScan = true;
                        app.qrCodeDesc = "打开小程序扫码";
                        app.checkQRCode(300, result.data.code);
                    }
                }
            });
        },
        checkQRCode(second, code) {
            clearInterval(app.timer);
            app.timer = setInterval(function () {
                second -= 2;
                if (second > 0) {
                    app.waitPhoneScan(app.timer, code);
                } else {
                    app.InvalidQRCode(app.timer);
                }
            }, 2000);
        },
        waitPhoneScan(timer, code) {
            $.ajax({
                url: "qrCodeLogin/check?code=" + code,
                dataType: "json",
                type: "get",
                success: function (result) {
                    if (result == null || result.data.status == null) {
                        app.InvalidQRCode(timer);
                        return;
                    }
                    switch (result.data.status) {
                        case '1':
                            app.qrCode = "img/qrcode_ok.png";
                            app.qrCodeDesc = "手机确认登录";
                            app.qrCodeName = result.data.account;
                            break;
                        case '2':
                            clearInterval(timer);
                            app.qrCodeSecret = code + "," + result.data.secret;
                            break;
                        case '3':
                            app.InvalidQRCode(timer);
                            break;
                        default:
                            break;
                    }
                }
            });
        },
        InvalidQRCode(timer) {
            app.qrCodeDesc = "点击图片刷新";
            app.allowScan = false;
            clearInterval(timer);
        },
        showUserPassword() {
            this.userPasswordVisible = true;
            this.phoneSmsCodeVisible = false;
            this.qrCodeVisible = false;
            this.loginType = "user_password";
            clearInterval(this.timer)
            this.clearError();
        },
        showPhoneSmsCode() {
            this.phoneSmsCodeVisible = true;
            this.userPasswordVisible = false;
            this.qrCodeVisible = false;
            this.loginType = "phone_smsCode";
            clearInterval(this.timer)
            this.clearError();
        },
        showQrCode() {
            this.qrCodeVisible = true;
            this.phoneSmsCodeVisible = false;
            this.userPasswordVisible = false;
            this.loginType = "qr_code";
            this.buildQRCode();
            this.clearError();
        },
        refreshQrCode() {
            this.buildQRCode();
        },
        clearError() {
            this.errorMsg = '';
            this.errorVisible = false;
        },
        showError(error) {
            if (error != null && error != '') {
                this.errorMsg = error;
            } else {
                this.errorMsg = $('#errorMsg').val();
            }
            if (this.errorMsg !== '' && this.errorMsg !== undefined) {
                this.errorVisible = true;
            }
        },
        initLoginData() {
            this.loginType = $('#loginType').val();
            switch (this.loginType) {
                case "phone_smsCode":
                    this.phone = $('#username').val();
                    this.showPhoneSmsCode();
                    break;
                case "qr_code":
                    this.showQrCode();
                    break;
                default:
                    this.username = $('#username').val();
                    this.showUserPassword();
                    break;
            }
        },
        resetCode() {
            let second = 60;
            let codeTime = setInterval(function () {
                second -= 1;
                if (second > 0) {
                    app.codeButton = second;
                    app.codeDisabled = true;
                } else {
                    clearInterval(codeTime);
                    app.codeButton = "验证码";
                    app.codeDisabled = false;
                }
            }, 1000);
        },
        createEnterPlugin(maxOutput) {
            const createCss = (index, d) => {
                const upd = d.toUpperCase();
                return {
                    [`*> .enter-${d}:nth-child(${index})`]: {
                        transform: `translate${upd}(50px)`
                    },
                    [`*> .-enter-${d}:nth-child(${index})`]: {
                        transform: `translate${upd}(-50px)`
                    },
                    [`* > .enter-${d}:nth-child(${index}),* > .-enter-${d}:nth-child(${index})`]: {
                        "z-index": `${10 - index}`,
                        opacity: "0",
                        animation: `enter-${d}-animation 0.4s ease-in-out 0.3s`,
                        "animation-fill-mode": "forwards",
                        "animation-delay": `${(index * 1) / 10}s`
                    }
                };
            };
            const handler = () => {
                const addRawCss = {};
                for (let index = 1; index < maxOutput; index++) {
                    Object.assign(addRawCss, {
                        ...createCss(index, "x"),
                        ...createCss(index, "y")
                    });
                }
                return {
                    ...addRawCss,
                    [`@keyframes enter-x-animation`]: {
                        to: {
                            opacity: "1",
                            transform: "translateX(0)"
                        }
                    },
                    [`@keyframes enter-y-animation`]: {
                        to: {
                            opacity: "1",
                            transform: "translateY(0)"
                        }
                    }
                };
            };
            return {handler};
        }
    },
}).mount('#newLogin')
app.$watch('qrCodeSecret', function () {
    $('#qrCodeLogin').submit();
});
