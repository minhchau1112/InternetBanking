import React, { useState } from "react";
import { Box, Button, TextField, Typography, IconButton, InputAdornment, Link } from "@mui/material";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import { useFormik } from "formik";
import * as yup from "yup";

const validationSchema = yup.object({
    username: yup.string().required("Tên đăng nhập là bắt buộc"),
    password: yup.string().min(6, "Mật khẩu phải có ít nhất 6 ký tự").required("Mật khẩu là bắt buộc"),
});

const Login: React.FC = () => {
    const [showPassword, setShowPassword] = useState<boolean>(false);

    const formik = useFormik({
        initialValues: {
            username: "",
            password: "",
        },
        validationSchema: validationSchema,
        onSubmit: (values) => {
            console.log("Dữ liệu Form", values);
        },
    });

    return (
        <Box
            sx={{
                width: "100vw",
                height: "100vh",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                backgroundColor: "#f9f9f9",
            }}
        >
            <Box
                sx={{
                    width: 640,
                    padding: 4,
                    backgroundColor: "white",
                    boxShadow: 3,
                    borderRadius: 2,
                    textAlign: "center",
                }}
            >
                <Typography
                    variant="h5"
                    sx={{
                        fontWeight: 700,
                        mb: 2,
                        fontSize: "30px",
                        lineHeight: "45px",
                        color: "#034B5E",
                    }}
                >
                    Đăng nhập
                </Typography>

                <Box component="form" onSubmit={formik.handleSubmit} noValidate>
                    <Typography
                        sx={{
                            fontWeight: 500,
                            fontSize: "14px",
                            lineHeight: "21px",
                            textAlign: "left",
                            color: "#919499",
                            mb: 0,
                        }}
                    >
                        Tên đăng nhập
                    </Typography>
                    <TextField
                        placeholder={"Tên đăng nhập"}
                        type="text"
                        fullWidth
                        variant="outlined"
                        margin="normal"
                        {...formik.getFieldProps("username")}
                        error={formik.touched.username && Boolean(formik.errors.username)}
                        helperText={formik.touched.username && formik.errors.username}
                        sx={{
                            "& .MuiFormHelperText-root": {
                                fontSize: "12px",
                                color: "#f44336",
                                marginTop: "5px",
                                marginLeft: "0px"
                            },
                        }}
                    />
                    <Typography
                        sx={{
                            fontWeight: 500,
                            fontSize: "14px",
                            lineHeight: "21px",
                            textAlign: "left",
                            color: "#919499",
                            mb: 0,
                            mt: 1
                        }}
                    >
                        Mật khẩu
                    </Typography>
                    <TextField
                        placeholder={"Mật khẩu"}
                        type={showPassword ? "text" : "password"}
                        fullWidth
                        variant="outlined"
                        margin="normal"
                        {...formik.getFieldProps("password")}
                        error={formik.touched.password && Boolean(formik.errors.password)}
                        helperText={formik.touched.password && formik.errors.password}
                        InputProps={{
                            endAdornment: (
                                <InputAdornment position="end">
                                    <IconButton
                                        onClick={() => setShowPassword(!showPassword)}
                                        edge="end"
                                    >
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
                                marginLeft: "0px"
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
                        color="warning"
                        sx={{
                            mt: 3,
                            textTransform: "none",
                            fontWeight: 600,
                            fontSize: "16px",
                            padding: "12px 0",
                            borderRadius: "8px",
                            backgroundColor: "#F8AD15",
                            "&:hover": {
                                backgroundColor: "#FB8C00",
                                boxShadow: "0 4px 10px rgba(0, 0, 0, 0.1)",
                            },
                            "&:focus": {
                                outline: "none",
                                boxShadow: "none",
                            },
                        }}
                        type="submit"
                    >
                        Đăng nhập
                    </Button>

                </Box>
            </Box>
        </Box>
    );
};

export default Login;
