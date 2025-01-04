import React from 'react';
import { useFormik } from 'formik';
import { z } from 'zod';
import { Input } from '@/components/ui/input.tsx';
import { Button } from '@/components/ui/button.tsx';
import { updatePassword } from '@/api/authAPI.ts';
import {toast} from "react-toastify";

interface ChangePasswordProps {
    onSuccess: () => void;
}

const ChangePassword: React.FC<ChangePasswordProps> = ({ onSuccess }) => {
    const passwordSchema = z.object({
        newPassword: z.string().min(5, 'Mật khẩu mới phải có ít nhất 5 ký tự'),
        confirmPassword: z.string().nonempty('Xác nhận mật khẩu là bắt buộc'),
    }).refine((data) => data.newPassword === data.confirmPassword, {
        message: 'Mật khẩu xác nhận không khớp',
        path: ['confirmPassword'],
    });

    const validate = (values: { newPassword: string; confirmPassword: string }) => {
        const result = passwordSchema.safeParse(values);
        if (!result.success) {
            return result.error.formErrors.fieldErrors;
        }
        return {};
    };

    const formik = useFormik({
        initialValues: {
            newPassword: '',
            confirmPassword: '',
        },
        validate,
        onSubmit: async (values) => {
            try {
                const username = localStorage.getItem('username') || 'customer';
                const passwordData = {
                    username,
                    password: values.newPassword,
                };

                await updatePassword(passwordData);

                toast.success('Mật khẩu của bạn đã được cập nhật thành công.');
                formik.resetForm();
                onSuccess();
            } catch (error) {
                console.error(error);
                toast.error('Có lỗi xảy ra khi thay đổi mật khẩu.');
            }
        },
    });

    return (
        <div className="grid gap-4 py-4">
            <form onSubmit={formik.handleSubmit}>
                <div className="mb-4">
                    <label htmlFor="newPassword" className="block text-sm font-medium text-gray-700">
                        Mật khẩu mới
                    </label>
                    <Input
                        id="newPassword"
                        name="newPassword"
                        type="password"
                        value={formik.values.newPassword}
                        onChange={formik.handleChange}
                        className="mt-1 block w-full"
                    />
                    {formik.touched.newPassword && formik.errors.newPassword && (
                        <p className="text-sm text-red-600">{formik.errors.newPassword}</p>
                    )}
                </div>

                <div className="mb-4">
                    <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700">
                        Xác nhận mật khẩu
                    </label>
                    <Input
                        id="confirmPassword"
                        name="confirmPassword"
                        type="password"
                        value={formik.values.confirmPassword}
                        onChange={formik.handleChange}
                        className="mt-1 block w-full"
                    />
                    {formik.touched.confirmPassword && formik.errors.confirmPassword && (
                        <p className="text-sm text-red-600">{formik.errors.confirmPassword}</p>
                    )}
                </div>

                <Button type="submit" className="w-full" disabled={formik.isSubmitting}>
                    {formik.isSubmitting ? 'Đang đổi...' : 'Đổi Mật Khẩu'}
                </Button>
            </form>
        </div>
    );
};

export default ChangePassword;