import { useDispatchUser } from "@/services/userContext/UserContextHelpers.ts";

function RemoveBeforeProdMvpUserTester() {
	const dispatch = useDispatchUser();

	function switchToPlayerB(): void {
		if (dispatch) {
			dispatch({
				type: "setPlayerB",
				user: null,
			});
		}
	}

	return (
		<button
			onClick={() => {
				switchToPlayerB();
			}}
		>
			Click me
		</button>
	);
}

export default RemoveBeforeProdMvpUserTester;
