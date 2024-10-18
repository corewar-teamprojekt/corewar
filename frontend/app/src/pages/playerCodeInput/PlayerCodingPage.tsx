import ProgrammInput from "@/components/mainContent/playerInput/ProgramInput";
import { useToast } from "@/hooks/use-toast";
import { uploadPlayerCode } from "@/services/rest/restService";
import { useUser } from "@/services/userContext/UserContextHelpers";
import BasePage from "../basePage/BasePage";

export default function PlayerCodingPage() {
	const user = useUser();

	const { toast } = useToast();

	function onProgramUploadClicked(code: string) {
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
			})
			.catch((error) => {
				toast({
					title: "Error uploading code: ",
					description: error.message,
					variant: "destructive",
				});
			});
	}

	return (
		<BasePage>
			<div className="flex flex-col justify-center items-center h-[100%] w-[100%]">
				<h1 className="text-2xl font-bold mb-2">Start coding:</h1>
				<ProgrammInput onProgramUploadClicked={onProgramUploadClicked} />
			</div>
		</BasePage>
	);
}
