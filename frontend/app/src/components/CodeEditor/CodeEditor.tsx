import { BASE_POLLING_INTERVAL_MS } from "@/consts";
import { Linterlint } from "@/domain/LinterLint";
import { usePageVisibility } from "@/lib/usePageVisibility";
import { getLinterLintsV1 } from "@/services/rest/RestService";
import { Editor, useMonaco } from "@monaco-editor/react";
import { ChangeEvent, useEffect, useRef, useState } from "react";
import { Button } from "../ui/button";
import {
	DropdownMenu,
	DropdownMenuContent,
	DropdownMenuItem,
	DropdownMenuLabel,
	DropdownMenuSeparator,
	DropdownMenuTrigger,
} from "../ui/dropdown-menu";
import { Input } from "../ui/input";
import { Separator } from "../ui/separator";
import { useToast } from "@/hooks/use-toast";
import { MarkerSeverity } from "monaco-editor/esm/vs/editor/editor.api";

interface CodeEditorProps {
	setProgram: (s: string) => void;
	program: string;
}

export default function CodeEditor({
	setProgram,
	program,
}: Readonly<CodeEditorProps>) {
	const [isDropdownOpen, setIsDropdownOpen] = useState(false);
	const monaco = useMonaco();
	const linterOwner = "redCodeLinter";
	const isPageVisible = usePageVisibility();
	const timerIdRef = useRef<ReturnType<typeof setInterval> | null>(null);
	const { toast } = useToast();

	useEffect(() => {
		const pollingCallback = async () => {
			const responseLints = await getLinterLintsV1(
				monaco?.editor.getModels()[0].getValue() ?? "",
			);
			setLinterLinting(responseLints);
		};

		const startPolling = () => {
			timerIdRef.current = setInterval(
				pollingCallback,
				BASE_POLLING_INTERVAL_MS,
			);
		};

		const stopPolling = () => {
			if (timerIdRef.current !== null) {
				clearInterval(timerIdRef.current);
				timerIdRef.current = null;
			}
		};

		if (isPageVisible) {
			startPolling();
		} else {
			stopPolling();
		}

		return () => {
			stopPolling();
		};
		// eslint is disabled because adding setLinterLinting to the dependency array would cause an infinite loop
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [isPageVisible, monaco]);

	function setLinterLinting(linterLints: Linterlint[]) {
		if (!monaco) return;
		const model = monaco.editor.getModels()[0];
		const markers = [];
		for (const ll of linterLints) {
			const marker = {
				severity: MarkerSeverity.Error,
				startLineNumber: ll.line,
				endLineNumber: ll.line,
				startColumn: ll.columnStart,
				endColumn: ll.columnEnd,
				message: ll.message,
				owner: linterOwner,
				resource: model.uri,
			};
			markers.push(marker);
		}
		monaco.editor.setModelMarkers(model, linterOwner, markers);
	}

	async function getFileContentAndSetCode(e: ChangeEvent<HTMLInputElement>) {
		const file = e.target.files?.[0];

		if (file) {
			const reader = new FileReader();
			const isIllegalRegex = /\ufffd/; //check if ureadable character is in file
			reader.onload = (event) => {
				const text = event.target?.result as string;
				if (!isIllegalRegex.test(text)) {
					setProgram(text);
				} else {
					toast({
						title: "Whoops, that is not a corewar program!",
						description: "Please check the file content and try again😢",
						variant: "destructive",
					});
				}
			};
			reader.readAsText(file);
		}
		setIsDropdownOpen(false);
	}

	function downloadCode() {
		setIsDropdownOpen(false);
		const element = document.createElement("a");
		const file = new Blob([program], { type: "text/plain" });
		element.href = URL.createObjectURL(file);
		element.download = "myCoreWarProgram.txt";
		document.body.appendChild(element);
		element.click();
		document.body.removeChild(element);
	}

	return (
		<>
			<div className="bg-neutral-900 flex flex-row items-center">
				<DropdownMenu open={isDropdownOpen}>
					<DropdownMenuTrigger onClick={() => setIsDropdownOpen(true)}>
						<div className="my-0.5 mx-2">File</div>
					</DropdownMenuTrigger>
					<DropdownMenuContent
						className=""
						onEscapeKeyDown={() => setIsDropdownOpen(false)}
						onPointerDownOutside={() => setIsDropdownOpen(false)}
					>
						<DropdownMenuLabel>File</DropdownMenuLabel>
						<DropdownMenuSeparator />
						<DropdownMenuItem>
							<label>
								upload
								<Input
									type="file"
									className="hidden w-[100%] h-[100%]"
									onChange={getFileContentAndSetCode}
								/>
							</label>
						</DropdownMenuItem>
						<DropdownMenuItem>
							<label>
								download
								<Button
									onClick={downloadCode}
									value="download"
									className="hidden"
								/>
							</label>
						</DropdownMenuItem>
					</DropdownMenuContent>
				</DropdownMenu>
				<Separator orientation="vertical" className="bg-neutral-600 h-[20px]" />
			</div>
			<Editor
				height="55vh"
				theme="corewarTheme"
				defaultLanguage="redcode"
				value={program}
				onMount={(editor) => setProgram(editor.getValue())}
				onChange={(value) => setProgram(value ?? "")}
			/>
		</>
	);
}
