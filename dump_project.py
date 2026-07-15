import os

def generate_project_summary(root_dir, output_file="project_context.txt"):
    ignored_dirs = {'.git', 'node_modules', '__pycache__', '.idea', '.vscode', 'venv', 'env', 'bin', 'obj'}
    ignored_files = {output_file, '.DS_Store', 'dump_project.py'}
    
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write("=== PROJECT STRUCTURE ===\n")
        for root, dirs, files in os.walk(root_dir):
            dirs[:] = [d for d in dirs if d not in ignored_dirs]
            level = root.replace(root_dir, '').count(os.sep)
            indent = ' ' * 4 * level
            f.write(f"{indent}{os.path.basename(root)}/\n")
            sub_indent = ' ' * 4 * (level + 1)
            for file in files:
                if file not in ignored_files:
                    f.write(f"{sub_indent}{file}\n")
        
        f.write("\n=== FILE CONTENTS ===\n")
        for root, dirs, files in os.walk(root_dir):
            dirs[:] = [d for d in dirs if d not in ignored_dirs]
            for file in files:
                if file in ignored_files:
                    continue
                file_path = os.path.join(root, file)
                relative_path = os.path.relpath(file_path, root_dir)
                try:
                    with open(file_path, 'r', encoding='utf-8') as code_file:
                        content = code_file.read()
                    f.write(f"\n---\nFile: {relative_path}\n---\n")
                    f.write(content)
                    f.write("\n")
                except (UnicodeDecodeError, PermissionError):
                    pass

if __name__ == "__main__":
    generate_project_summary(os.getcwd())