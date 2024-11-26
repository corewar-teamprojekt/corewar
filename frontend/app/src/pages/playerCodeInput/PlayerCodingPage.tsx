import ConfirmActionDialog from "@/components/confirmActionDialog/ConfirmActionDialog";
import ProgrammInput from "@/components/ProgramInput/ProgramInput";
import { RequireUser } from "@/components/requireUser.tsx/RequireUser";
import { useToast } from "@/hooks/use-toast";
import { useUser } from "@/services/userContext/UserContextHelpers";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { submitCodeV1 } from "@/services/rest/LobbyRest.ts";
import { useLobby } from "@/services/lobbyContext/LobbyContextHelpers.ts";

export default function PlayerCodingPageV2() {
	const navigate = useNavigate();
	const user = useUser();
	const lobby = useLobby();
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
		if (!lobby) {
			console.error("No lobby provided");
			return;
		}
		submitCodeV1(lobby.id, user.name, code)
			.then((response) => {
				if (response.status >= 200 && response.status < 300) {
					displaySuccessToastAndNavigate();
				} else {
					displayErrorToast(
						"Something went wrong while uploading your code :(",
					);
				}
			})
			.catch((error) => {
				displayErrorToast(error.message);
			});
	}

	function displaySuccessToastAndNavigate() {
		toast({
			title: "Success!",
			description: "your code has been uploaded",
		});
		navigate("/waiting-for-opponent");
	}

	function displayErrorToast(errorMessage: string) {
		toast({
			title: "Error uploading code: ",
			description: errorMessage,
			variant: "destructive",
		});
	}

	return (
		<RequireUser>
			<div className="flex flex-col justify-center items-center h-[100%] w-[100%]">
				<h1 className="text-2xl font-bold mb-2">Start coding:</h1>
				<ProgrammInput onProgramUploadClicked={setCodeAndOpenDialog} />
			</div>
			<ConfirmActionDialog
				isOpen={isConfirmDialogOpen}
				setIsOpen={setIsConfirmDialogOpen}
				onConfirm={triggerCodeUpload}
				title="Upload code"
				description="Are you sure you want to upload this code?"
			/>
		</RequireUser>
	);
}
