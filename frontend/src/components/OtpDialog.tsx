import React, { useState } from 'react';
import { Dialog, DialogActions, DialogContent, DialogTitle, TextField, Button } from '@mui/material';

interface OtpDialogProps {
  open: boolean;
  onClose: () => void;
  onConfirm: (otp: string) => void;
  errorMessage: string;
}

const OtpDialog: React.FC<OtpDialogProps> = ({ open, onClose, onConfirm, errorMessage }) => {
  const [otp, setOtp] = useState('');
  const [localErrorMessage, setLocalErrorMessage] = useState(errorMessage);

  const handleOtpChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setOtp(e.target.value);
    setLocalErrorMessage(''); // Reset error message when user starts typing again
  };

  const handleConfirm = () => {
    if (otp.trim()) {
      onConfirm(otp);
    } else {
      setLocalErrorMessage('OTP cannot be empty');
    }
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Enter OTP</DialogTitle>
      <DialogContent>
        <TextField
          autoFocus
          margin="dense"
          label="OTP"
          type="text"
          fullWidth
          variant="standard"
          value={otp}
          onChange={handleOtpChange}
          error={!!localErrorMessage}
          helperText={localErrorMessage || errorMessage}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="secondary">
          Cancel
        </Button>
        <Button onClick={handleConfirm} color="primary">
          Confirm
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default OtpDialog;