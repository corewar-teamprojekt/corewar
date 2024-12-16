import { useState } from "react";
import ConfirmActionDialog from "../confirmActionDialog/ConfirmActionDialog";
import { Button } from "../ui/button";
import {
	Card,
	CardDescription,
	CardFooter,
	CardHeader,
	CardTitle,
} from "../ui/card";

interface CodeExampleCardProps {
	title: string;
	shortDescription: string;
	setCode: () => void;
}

export function CodeExampleCard({
	setCode,
	title,
	shortDescription,
}: Readonly<CodeExampleCardProps>) {
	const [isConfirmDialogOpen, setConfirmDialogOpen] = useState(false);

	const confirmDialogTitle = "Paste example code into editor";
	const confirmDialogDescription =
		"Are you sure you want to paste the example code into the editor? This will overwrite your current code.";

	return (
		<>
			<Card className="bg-zinc-950 bg-opacity-50">
				<CardHeader>
					<CardTitle>{title}</CardTitle>
					<CardDescription>{shortDescription}</CardDescription>
				</CardHeader>
				<CardFooter>
					<Button
						onClick={() => setConfirmDialogOpen(true)}
						variant="secondary"
					>
						Paste code
					</Button>
				</CardFooter>
			</Card>
			<ConfirmActionDialog
				onConfirm={setCode}
				isOpen={isConfirmDialogOpen}
				setIsOpen={setConfirmDialogOpen}
				title={confirmDialogTitle}
				description={confirmDialogDescription}
			/>
		</>
	);
}
