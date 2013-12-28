# pre-commit.sh

cd "$(dirname ${BASH_SOURCE[0]})"

git rev-parse HEAD > app/src/main/assets/version.txt
