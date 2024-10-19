import ConfirmActionDialog from "@/components/confirmActionDialog/ConfirmActionDialog";
import ProgrammInput from "@/components/mainContent/playerInput/ProgramInput";
import { useToast } from "@/hooks/use-toast";
import { uploadPlayerCode } from "@/services/rest/restService";
import { useUser } from "@/services/userContext/UserContextHelpers";
import { useState } from "react";
import BasePage from "../basePage/BasePage";

export default function PlayerCodingPage() {
	const user = useUser();
	const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false);
	const [code, setCode] = useState("");
	const { toast } = useToast();

	function setCodeAndOpenDialog(code: string) {
		setCode(code);
		setIsConfirmDialogOpen(true);
	}

	function triggerCodeUpload() {
		if (!user) {
			console.error("No user provided");
			return;
		}
		uploadPlayerCode(user.name, code)
			.then(() => {
				toast({
					title: "Success!",
					description: "your code has been uploaded",
				});
				setTimeout(goToWaitingForResult, 1000);
			})
			.catch((error) => {
				toast({
					title: "Error uploading code: ",
					description: error.message,
					variant: "destructive",
				});
			});
	}

	function goToWaitingForResult() {
		window.location.href = "/waiting-for-result";
	}

	return (
		<BasePage>
			<div className="flex flex-col justify-center items-center h-[100%] w-[100%]">
				<h1 className="text-2xl font-bold mb-2">Start coding:</h1>
				<ProgrammInput onProgramUploadClicked={setCodeAndOpenDialog} />
			</div>
			<ConfirmActionDialog
				isOpen={isConfirmDialogOpen}
				setIsOpen={setIsConfirmDialogOpen}
				onConfirm={triggerCodeUpload}
			/>
		</BasePage>
	);
}
