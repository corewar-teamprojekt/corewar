import { DialogClose } from "@radix-ui/react-dialog";
import { Button } from "../ui/button";
import {
	Dialog,
	DialogContent,
	DialogDescription,
	DialogHeader,
	DialogTitle,
} from "../ui/dialog";

interface ConfirmActionDialogProps {
	onConfirm: () => void;
	onCancel: () => void | undefined;
	title: string | undefined;
	description: string | undefined;
	isOpen: boolean;
}

export default function ConfirmActionDialog({
	onConfirm,
	onCancel = () => {},
	title = "Are you sure ?",
	description = "This action might be irreversible",
	isOpen,
}: Readonly<ConfirmActionDialogProps>) {
	return (
		<Dialog open={isOpen}>
			<DialogContent>
				<DialogHeader>
					<DialogTitle>{title}</DialogTitle>
					<DialogDescription>{description}</DialogDescription>
				</DialogHeader>
				<DialogContent>
					<DialogClose asChild>
						<Button onClick={onConfirm}>Confirm</Button>
						<Button onClick={onCancel}>Cancel</Button>
					</DialogClose>
				</DialogContent>
			</DialogContent>
		</Dialog>
	);
}
