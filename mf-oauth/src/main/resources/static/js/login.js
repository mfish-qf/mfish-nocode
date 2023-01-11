let vm = new Vue({
    el: '#ssoLogin',
    data: {
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
    },
    mounted: function () {
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
                        vm.codeValue = result.data;
                        vm.resetCode();
                    } else {
                        vm.showError(result.data.msg);
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
                        vm.captchaUrl = "data:image/gif;base64," + result.data.img;
                        vm.captchaKey = result.data.captchaKey;
                    } else {
                        vm.showError(result.data.msg);
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
                        vm.qrCode = result.data.img;
                        vm.allowScan = true;
                        vm.qrCodeDesc = "打开小程序扫码";
                        vm.checkQRCode(300, result.data.code);
                    }
                }
            });
        },
        checkQRCode(second, code) {
            clearInterval(vm.timer);
            vm.timer = setInterval(function () {
                second -= 2;
                if (second > 0) {
                    vm.waitPhoneScan(vm.timer, code);
                } else {
                    vm.InvalidQRCode(vm.timer);
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
                        vm.InvalidQRCode(timer);
                        return;
                    }
                    switch (result.data.status) {
                        case '1':
                            vm.qrCode = "img/qrcode_ok.png";
                            vm.qrCodeDesc = "手机确认登录";
                            vm.qrCodeName = result.data.account;
                            break;
                        case '2':
                            clearInterval(timer);
                            vm.qrCodeSecret = code + "," + result.data.secret;
                            break;
                        case '3':
                            vm.InvalidQRCode(timer);
                            break;
                        default:
                            break;
                    }
                }
            });
        },
        InvalidQRCode(timer) {
            vm.qrCodeDesc = "点击图片刷新";
            vm.allowScan = false;
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
                    vm.codeButton = second;
                    vm.codeDisabled = true;
                } else {
                    clearInterval(codeTime);
                    vm.codeButton = "验证码";
                    vm.codeDisabled = false;
                }
            }, 1000);
        }
    }
});
vm.$watch('qrCodeSecret', function () {
    $('#qrCodeLogin').submit();
});