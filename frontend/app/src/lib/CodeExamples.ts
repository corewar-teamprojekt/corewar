import { CodeExample } from "@/domain/CodeExample";

export const codeExamples: CodeExample[] = [
	{
		title: "Dwarf",
		shortDescription:
			'The dwarf "bombs" the core at regular intervals with DATs while making sure to not hit itself',
		code: "add.ab  #4, 3\nmov.i   2, @2\njmp     -2\ndat     #0, #0\n",
	},
	{
		title: "Mice",
		shortDescription:
			"This compact, aggressive warrior exploits quick replication and parallel execution to overwhelm opponents through sheer numerical advantage.",
		code: "mov.ab  #12, -1\nmov.i   @-2, <5\ndjn     -1, -3\nspl     @3, 0\nadd.ab  #653, 2\njmz     -5, -6\ndat     #0, #833\n",
	},
	{
		title: "Scanner",
		shortDescription:
			"This minimalist scanner searches for enemy code using incremental addressing, then attacks detected targets by copying instructions to their location.",
		code: "add    #10, 1\njmz.f  -1, 5\nmov.i  2, >-1\njmp    -1\n",
	},
];
