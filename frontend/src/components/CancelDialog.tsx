import React, { useState } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from '@mui/material';

interface CancelDialogProps {
	open: boolean;
	onClose: () => void;
	onConfirm: (reason: string) => void;
}

const CancelDialog: React.FC<CancelDialogProps> = ({ open, onClose, onConfirm }) => {
	const [reason, setReason] = useState('');

	const handleConfirm = () => {
		onConfirm(reason);

		onClose();
	};

	return (
		<Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
			<DialogTitle>Cancel Debt Reminder</DialogTitle>
			<DialogContent>
				<TextField
					label="Reason"
					fullWidth
					multiline
					rows={5}
					value={reason}
					onChange={(e) => setReason(e.target.value)}
				/>
			</DialogContent>
			<DialogActions>
				<Button onClick={onClose} color="secondary" variant="outlined">
					Cancel
				</Button>
				<Button onClick={handleConfirm} color="primary" variant="contained" disabled={!reason.trim()}>
					Confirm
				</Button>
			</DialogActions>
		</Dialog>
	);
};

export default CancelDialog;
