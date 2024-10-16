import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Textarea } from "@/components/ui/textarea";
import { ChangeEvent, useState } from "react";

interface ProgrammInputProps {
	onProgramUploadClicked: (s: string) => void;
}

export default function ProgrammInput({
	onProgramUploadClicked,
}: Readonly<ProgrammInputProps>) {
	const [inputType, setInputType] = useState("coding");
	const [program, setProgram] = useState("");

	async function getFileContentAndSetCode(e: ChangeEvent<HTMLInputElement>) {
		const file = e.target.files?.[0];
		if (file) {
			const text = await file.text();
			setProgram(text);
		}
	}

	return (
		<Card className="w-1/4 min-w-[360px]">
			<CardContent className="flex flex-col items-center">
				<Tabs
					defaultValue="coding"
					className="w-max flex flex-col items-center mt-3"
					onValueChange={setInputType}
				>
					<TabsList className="mb-2">
						<TabsTrigger value="coding">Type code</TabsTrigger>
						<TabsTrigger value="upload">File upload</TabsTrigger>
					</TabsList>
					<Textarea
						value={program}
						onChange={(e) => setProgram(e.target.value)}
						readOnly={inputType !== "coding"}
						className="w-[350px] h-[200px] mb-3"
					/>
					{/* display upload field, when inputType is upload, else display placeholder */}
					{inputType === "upload" ? (
						<Input
							type="file"
							className="w-[300px] h-[40px]"
							onChange={getFileContentAndSetCode}
						/>
					) : (
						<div className="h-[40px]" />
					)}
				</Tabs>
			</CardContent>
			<CardFooter className="flex flex-col items-center">
				<Button onClick={() => onProgramUploadClicked(program)}>upload</Button>
			</CardFooter>
		</Card>
	);
}
