import React, { useState } from "react";
import { Box, Button, TextField, Typography, IconButton, InputAdornment, Link } from "@mui/material";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import axios from "axios";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const validationSchema = z.object({
    username: z.string().min(1, "Tên đăng nhập là bắt buộc"),
    password: z
        .string()
        .min(6, "Mật khẩu phải có ít nhất 6 ký tự")
        .min(1, "Mật khẩu là bắt buộc"),
});

type FormValues = z.infer<typeof validationSchema>;

const Login: React.FC = () => {
    const [showPassword, setShowPassword] = useState<boolean>(false);

    const { register, handleSubmit, formState: { errors } } = useForm<FormValues>({
        resolver: zodResolver(validationSchema),
    });

    const handleLogin = async (values: FormValues) => {
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
        <Box
            sx={{
                width: "100vw",
                height: "100vh",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                backgroundColor: "#FFF",
                color: "#000",
            }}
        >
            <Box
                sx={{
                    width: 480,
                    padding: 4,
                    backgroundColor: "#FFF",
                    boxShadow: 3,
                    borderRadius: 2,
                    textAlign: "center",
                    border: "1px solid #E0E0E0",
                }}
            >
                <Typography
                    variant="h5"
                    sx={{
                        fontWeight: 700,
                        mb: 3,
                        fontSize: "30px",
                        lineHeight: "45px",
                        color: "#333",
                        fontFamily: "'Roboto', sans-serif",
                    }}
                >
                    Đăng nhập
                </Typography>

                <form onSubmit={handleSubmit(handleLogin)} noValidate>
                    <Typography
                        sx={{
                            fontWeight: "bold",
                            fontSize: "14px",
                            lineHeight: "21px",
                            textAlign: "left",
                            color: "#555",
                            mb: 0,
                        }}
                    >
                        Tên đăng nhập
                    </Typography>
                    <TextField
                        placeholder="Tên đăng nhập"
                        type="text"
                        fullWidth
                        variant="outlined"
                        margin="normal"
                        {...register("username")}
                        error={Boolean(errors.username)}
                        helperText={errors.username?.message}
                        sx={{
                            "& .MuiFormHelperText-root": {
                                fontSize: "12px",
                                color: "#f44336",
                                marginTop: "5px",
                                marginLeft: "0px",
                            },
                            "& .MuiOutlinedInput-root": {
                                borderRadius: "8px",
                                borderColor: "#BDBDBD",
                            },
                        }}
                    />

                    <Typography
                        sx={{
                            fontWeight: "bold",
                            fontSize: "14px",
                            lineHeight: "21px",
                            textAlign: "left",
                            color: "#555",
                            mb: 0,
                            mt: 1
                        }}

                    >
                        Mật khẩu
                    </Typography>
                    <TextField
                        placeholder="Mật khẩu"
                        type={showPassword ? "text" : "password"}
                        fullWidth
                        variant="outlined"
                        margin="normal"
                        {...register("password")}
                        error={Boolean(errors.password)}
                        helperText={errors.password?.message}
                        InputProps={{
                            endAdornment: (
                                <InputAdornment position="end">
                                    <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                                        {showPassword ? <VisibilityOff /> : <Visibility />}
                                    </IconButton>
                                </InputAdornment>
                            ),
                        }}
                        sx={{
                            "& .MuiFormHelperText-root": {
                                fontSize: "12px",
                                color: "#f44336",
                                marginTop: "5px",
                                marginLeft: "0px",
                            },
                            "& .MuiOutlinedInput-root": {
                                borderRadius: "8px",
                                borderColor: "#BDBDBD",
                            },
                        }}
                    />
                    <Box sx={{ textAlign: "left", mt: 1 }}>
                        <Link href="#" variant="body2" color="primary">
                            Quên mật khẩu?
                        </Link>
                    </Box>

                    <Button
                        fullWidth
                        variant="contained"
                        color="primary"
                        sx={{
                            mt: 3,
                            textTransform: "none",
                            fontWeight: 600,
                            fontSize: "16px",
                            padding: "12px 0",
                            borderRadius: "8px",
                            backgroundColor: "#333",
                            "&:focus": {
                                outline: "none",
                                boxShadow: "none",
                            },
                        }}
                        type="submit"
                    >
                        Đăng nhập
                    </Button>
                </form>
            </Box>

            <ToastContainer />
        </Box>
    );
};

export default Login;
