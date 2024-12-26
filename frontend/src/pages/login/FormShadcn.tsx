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
import {toast} from "react-toastify";

const formSchema = z.object({
    username: z.string().min(1, "Tên đăng nhập là bắt buộc"),
    password: z
        .string()
        .min(6, "Mật khẩu phải có ít nhất 6 ký tự")
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
            const response = await axios.post(
                "http://localhost:8888/api/auth/login",
                { username: values.username, password: values.password },
                { withCredentials: true }
            );

            localStorage.setItem("access_token", response.data.data.accessToken);
            localStorage.setItem("accountId", response.data.data.user.accountID);

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
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8 w-[480px] bg-white">
                    <FormField
                        control={form.control}
                        name="username"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>Tên đăng nhập</FormLabel>
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
                                <FormLabel>Mật khẩu</FormLabel>
                                <FormControl>
                                    <Input placeholder="Mật khẩu" {...field} />
                                </FormControl>
                                <FormMessage/>
                            </FormItem>
                        )}
                    />
                    <Button className="w-full" type="submit">Đăng nhập</Button>
                </form>
            </div>
        </Form>
    )
}