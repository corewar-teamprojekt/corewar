import { useDispatchUser } from "@/services/UserContext.tsx";

function Button() {
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

export default Button;
