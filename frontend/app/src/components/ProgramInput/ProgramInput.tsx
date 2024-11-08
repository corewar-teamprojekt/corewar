import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { useState } from "react";
import CodeEditor from "../CodeEditor/CodeEditor";

interface ProgrammInputProps {
	onProgramUploadClicked: (s: string) => void;
}

export default function ProgrammInput({
	onProgramUploadClicked,
}: Readonly<ProgrammInputProps>) {
	const [program, setProgram] = useState("");

	return (
		<Card className="w-[50%] min-w-[360px]">
			<CardContent className="flex flex-col">
				<div className="mt-3 w-[100%] border-2 border-slate-500">
					<CodeEditor setProgram={setProgram} program={program} />
				</div>
			</CardContent>
			<CardFooter className="flex flex-col items-center">
				<Button onClick={() => onProgramUploadClicked(program)}>
					upload code
				</Button>
			</CardFooter>
		</Card>
	);
}
