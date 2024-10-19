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
	isOpen: boolean;
	setIsOpen: (value: boolean) => void;
	onCancel?: () => void;
	title?: string;
	description?: string;
}

export default function ConfirmActionDialog({
	onConfirm,
	isOpen,
	setIsOpen,
	onCancel = () => {},
	title = "Are you sure ?",
	description = "This action might be irreversible",
}: Readonly<ConfirmActionDialogProps>) {
	return (
		<Dialog open={isOpen} onOpenChange={setIsOpen}>
			<DialogContent>
				<DialogHeader>
					<DialogTitle>{title}</DialogTitle>
					<DialogDescription>{description}</DialogDescription>
				</DialogHeader>
				<div className="w-[100%] flex justify-center gap-x-1">
					<DialogClose asChild>
						<Button onClick={onConfirm}>Confirm</Button>
					</DialogClose>
					<DialogClose asChild>
						<Button onClick={onCancel} variant={"destructive"}>
							Cancel
						</Button>
					</DialogClose>
				</div>
			</DialogContent>
		</Dialog>
	);
}
