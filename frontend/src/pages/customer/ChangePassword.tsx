import React, { useState } from 'react';
import { useFormik } from 'formik';
import { z } from 'zod';
import { Input } from '@/components/ui/input.tsx';
import { Button } from '@/components/ui/button.tsx';

const ChangePassword: React.FC = () => {
    const [successMessage, setSuccessMessage] = useState('');

    const passwordSchema = z.object({
        currentPassword: z.string().nonempty('Mật khẩu hiện tại là bắt buộc'),
        newPassword: z.string().min(5, 'Mật khẩu mới phải có ít nhất 5 ký tự'),
        confirmPassword: z.string().nonempty('Xác nhận mật khẩu là bắt buộc'),
    }).refine((data) => data.newPassword === data.confirmPassword, {
        message: 'Mật khẩu xác nhận không khớp',
        path: ['confirmPassword'],
    });

    const validate = (values: { currentPassword: string; newPassword: string; confirmPassword: string }) => {
        const result = passwordSchema.safeParse(values);
        if (!result.success) {
            return result.error.formErrors.fieldErrors;
        }
        return {};
    };

    const formik = useFormik({
        initialValues: {
            currentPassword: '',
            newPassword: '',
            confirmPassword: '',
        },
        validate,
        onSubmit: (values) => {
            setTimeout(() => {
                console.log('Đổi mật khẩu thành công:', values);
                setSuccessMessage('Mật khẩu của bạn đã được cập nhật thành công.');
                formik.resetForm();
            }, 1000);
        },
    });

    return (
        <div className="grid gap-4 py-4">
            {successMessage && (
                <div className="mb-4 p-2 text-green-700 bg-green-100 rounded">
                    {successMessage}
                </div>
            )}
            <form onSubmit={formik.handleSubmit}>
                <div className="mb-4">
                    <label htmlFor="currentPassword" className="block text-sm font-medium text-gray-700">
                        Mật khẩu hiện tại
                    </label>
                    <Input
                        id="currentPassword"
                        name="currentPassword"
                        type="password"
                        value={formik.values.currentPassword}
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        className="mt-1 block w-full mb-2"
                    />
                    {formik.touched.currentPassword && formik.errors.currentPassword && (
                        <p className="text-sm text-red-600">{formik.errors.currentPassword}</p>
                    )}
                </div>

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
                        onBlur={formik.handleBlur}
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
                        onBlur={formik.handleBlur}
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
