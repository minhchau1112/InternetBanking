import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"
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

const formSchema = z.object({
    username: z.string().min(1, "Tên đăng nhập là bắt buộc"),
    password: z
        .string()
        .min(5, "Mật khẩu phải có ít nhất 6 ký tự")
        .min(1, "Mật khẩu là bắt buộc"),
})

export function LoginForm() {
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            username: "",
            password: "",
        },
    })

    const onSubmit = async (values: z.infer<typeof formSchema>) => {
        try {
            const data = await login(values.username, values.password);

            localStorage.setItem("access_token", data.accessToken);
            localStorage.setItem("accountId", data.user.accountID);

            toast.success("Đăng nhập thành công.");
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
            <div className="flex items-center justify-center w-screen bg-white h-screen">
                <form onSubmit={form.handleSubmit(onSubmit)}
                      className="space-y-8 w-[480px] bg-white p-8 rounded-lg shadow-lg">
                    <div className="text-center space-y-2">
                        <h1 className="text-2xl font-bold text-gray-800">
                            Internet Banking
                        </h1>
                        <p className="text-sm text-gray-500">
                            Đăng nhập để tiếp tục sử dụng dịch vụ
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
                    <Button className="w-full hover:border-none" type="submit">Đăng nhập</Button>
                    <div className="text-left">
                        <a
                            href="/forgot-password"
                            className="text-sm"
                        >
                            Quên mật khẩu?
                        </a>
                    </div>
                </form>
            </div>
            <ToastContainer/>
        </Form>
    )
}