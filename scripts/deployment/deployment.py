import os
import re
import subprocess
import logging
from time import sleep

from flask import Flask, jsonify, request
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

app = Flask(__name__)
limiter = Limiter(get_remote_address, default_limits=["250 per day", "15 per hour"])

# Configuration
IMAGE_URL = "ghcr.io/corewar-teamprojekt/aio:{}"
DOMAIN_TEMPLATE = "{}.corewar.shonk.software"
PRE_SHARED_KEY = os.getenv("KEY")

VALID_REF_PATTERN = r"^[a-zA-Z0-9\.\-\+\_\/\\]+$"


def run_command(command):
    """Helper function to run shell commands."""
    try:
        result = subprocess.run(command, check=True, text=True, capture_output=True)
        logging.info(f"Command executed: {' '.join(command)}")
        logging.info(f"Command output: {result.stdout.strip()}")
        return result.stdout.strip(), None
    except subprocess.CalledProcessError as e:
        logging.error(f"Command failed: {' '.join(command)}")
        return None, e.stderr.strip()


def authenticate(request):
    """Check for pre-shared key in the request headers."""
    valid = request.headers.get("Authorization") == f"Bearer {PRE_SHARED_KEY}"
    if not valid:
        logging.error(
            "Unauthorized request with invalid pre-shared key: %s",
            request.headers.get("Authorization"),
        )
    return valid


def get_domain_name(ref):
    """Generate the domain name for the deployment."""
    ref = (
        ref.replace("/", "-")
        .replace(":", "-")
        .replace(".", "-")
        .replace("\\", "-")
        .replace("+", "-")
        .replace("_", "-")
    )
    return DOMAIN_TEMPLATE.format(ref)


def validate_reference(ref):
    """Validate the reference against the regex pattern."""
    if not re.match(VALID_REF_PATTERN, ref):
        logging.error("Invalid reference format: %s", ref)
        return (
            False,
            "Invalid reference format. Only alphanumeric characters, hyphens, underscores, plus signs, dots, slashes, and backslashes are allowed.",
        )
    return True, ""


@app.route("/deployments/<ref>", methods=["POST"])
def create_or_update_deployment(ref):
    if not authenticate(request):
        sleep(3)
        return jsonify({"error": "Unauthorized"}), 401

    # Validate the reference
    is_valid, error = validate_reference(ref)
    if not is_valid:
        return jsonify({"error": error}), 400

    image = IMAGE_URL.format(ref)
    domain = get_domain_name(ref)

    # Run Podman command to create/update the container
    command = [
        "podman",
        "run",
        "-d",
        "--name",
        ref,
        "--replace",
        "--network=web",
        "--expose=80",
        "--restart=always",
        "--pull",
        "always",
        "--label",
        f"traefik.http.routers.{ref}.rule=Host(`{domain}`)",
        "--label",
        f"traefik.http.services.{ref}.loadbalancer.server.port=80",
        image,
    ]

    _, error = run_command(command)

    if error:
        logging.error("Error creating/updating deployment: %s", error)
        return jsonify("Internal Server Error"), 500

    return jsonify(
        {
            "message": f"Deployment created/updated for {ref}",
            "url": "https://" + domain,
        }
    ), 200


@app.route("/deployments/<ref>", methods=["DELETE"])
@limiter.limit("1 per hour")
def delete_deployment(ref):
    if not authenticate(request):
        sleep(3)
        return jsonify({"error": "Unauthorized"}), 401

    # Validate the reference
    is_valid, error = validate_reference(ref)
    if not is_valid:
        return jsonify({"error": error}), 400

    image = IMAGE_URL.format(ref)

    # Run Podman command to stop and remove the container
    stop_command = ["podman", "stop", "-i", ref]
    remove_command = ["podman", "rm", "-i", ref]
    delete_image_command = ["podman", "image", "rm", "-i", image]

    _, stop_error = run_command(stop_command)
    if stop_error:
        logging.error("Error stopping deployment: %s", stop_error)
        return jsonify("Internal Server Error"), 500

    _, remove_error = run_command(remove_command)
    if remove_error:
        logging.error("Error deleting deployment: %s", remove_error)
        return jsonify("Internal Server Error"), 500

    _, image_error = run_command(delete_image_command)
    if image_error:
        logging.error("Error deleting deployment: %s", image_error)
        return jsonify("Internal Server Error"), 500

    return jsonify({"message": f"Deployment deleted for {ref}"}), 200


@app.route("/status")
def status():
    return jsonify({"status": "OK"}), 200


if __name__ == "__main__":
    # Set the Flask app to run on port 5000
    logging.basicConfig(level=logging.INFO, filename="deployment.log")
    app.run(host="0.0.0.0", port=5000)
