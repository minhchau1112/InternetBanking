import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button"
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import axios from "axios";
import {toast, ToastContainer} from "react-toastify";
import {login} from "@/api/authAPI.ts";
import {useDispatch} from "react-redux";
import {setUser} from "@/redux/slices/authSlice.ts";
import ReCAPTCHA from "react-google-recaptcha";
import {useState} from "react";

const formSchema = z.object({
    username: z.string().min(1, "Tên đăng nhập là bắt buộc"),
    password: z
        .string()
        .min(5, "Mật khẩu phải có ít nhất 5 ký tự")
        .min(1, "Mật khẩu là bắt buộc"),
})
const RECAPTCHA_SITE_KEY = "6LeOfqoqAAAAABX7HwReZGjialYSZF-RQ6MjvwTn";

export function LoginForm() {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const [recaptchaValue, setRecaptchaValue] = useState<string | null>(null);
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            username: "",
            password: "",
        },
    })
    const onRecaptchaChange = (value: string | null) => {
        setRecaptchaValue(value);
    };
    const onForgotPassword = async () => {
        try {
            navigate("/forgot-password");
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra khi gửi OTP. Vui lòng thử lại.");
        }
    };

    const onSubmit = async (values: z.infer<typeof formSchema>) => {
        if (!recaptchaValue) {
            toast.error("Vui lòng xác minh reCAPTCHA");
            return;
        }
        try {
            const data = await login(values.username, values.password, recaptchaValue);

            console.log(data);

            localStorage.setItem("access_token", data.accessToken);
            localStorage.setItem("accountId", data.user.accountID);

            dispatch(setUser(data.user));

            toast.success("Đăng nhập thành công.");

            setTimeout(() => {
                navigate("/");
            }, 3000);
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error("Đăng nhập thất bại, vui lòng kiểm tra lại thông tin.");
            } else {
                toast.error("Có lỗi xảy ra, vui lòng thử lại.");
            }
        }
    };
    return (
        <Form {...form}>
            <div className="flex items-center justify-center w-screen bg-gray-100 h-screen">
                <form onSubmit={form.handleSubmit(onSubmit)}
                      className="space-y-6 w-[480px] bg-white p-8 rounded-lg shadow-lg">
                    <div className="text-center space-y-1">
                        <h1 className="text-2xl font-bold text-gray-800">
                            Internet Banking
                        </h1>
                        <p className="text-sm text-gray-500">
                            Chào mừng, đăng nhập để tiếp tục.
                        </p>
                    </div>

                    <FormField
                        control={form.control}
                        name="username"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel className="font-bold">Tên đăng nhập</FormLabel>
                                <FormControl>
                                    <Input placeholder="Tên đăng nhập" {...field} />
                                </FormControl>
                                <FormMessage/>
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="password"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel className="font-bold">Mật khẩu</FormLabel>
                                <FormControl>
                                    <Input placeholder="Mật khẩu" {...field} />
                                </FormControl>
                                <FormMessage/>
                            </FormItem>
                        )}
                    />
                    <ReCAPTCHA
                        sitekey={RECAPTCHA_SITE_KEY}
                        onChange={onRecaptchaChange}
                    />
                    <Button className="w-full hover:border-none" type="submit">Đăng nhập</Button>
                    <div className="text-left">
                        <button
                            type="button"
                            onClick={onForgotPassword}
                            className="text-sm text-blue-600 bg-transparent border-none p-0 m-0 focus:outline-none"
                        >
                            Quên mật khẩu?
                        </button>
                    </div>
                </form>
            </div>
            <ToastContainer/>
        </Form>
    )
}