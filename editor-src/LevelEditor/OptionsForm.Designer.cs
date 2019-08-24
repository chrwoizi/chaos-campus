namespace Javy
{
    partial class OptionsForm
    {
        /// <summary>
        /// Erforderliche Designervariable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Verwendete Ressourcen bereinigen.
        /// </summary>
        /// <param name="disposing">True, wenn verwaltete Ressourcen gelöscht werden sollen; andernfalls False.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Vom Windows Form-Designer generierter Code

        /// <summary>
        /// Erforderliche Methode für die Designerunterstützung.
        /// Der Inhalt der Methode darf nicht mit dem Code-Editor geändert werden.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(OptionsForm));
            this.wtkPathEdit = new System.Windows.Forms.TextBox();
            this.WtkSettingsBox = new System.Windows.Forms.GroupBox();
            this.browseButton = new System.Windows.Forms.Button();
            this.wtkPathLabel = new System.Windows.Forms.Label();
            this.okayButton = new System.Windows.Forms.Button();
            this.cancelButton = new System.Windows.Forms.Button();
            this.wtkBrowser = new System.Windows.Forms.FolderBrowserDialog();
            this.WtkSettingsBox.SuspendLayout();
            this.SuspendLayout();
            // 
            // wtkPathEdit
            // 
            resources.ApplyResources(this.wtkPathEdit, "wtkPathEdit");
            this.wtkPathEdit.Name = "wtkPathEdit";
            // 
            // WtkSettingsBox
            // 
            this.WtkSettingsBox.Controls.Add(this.browseButton);
            this.WtkSettingsBox.Controls.Add(this.wtkPathLabel);
            this.WtkSettingsBox.Controls.Add(this.wtkPathEdit);
            resources.ApplyResources(this.WtkSettingsBox, "WtkSettingsBox");
            this.WtkSettingsBox.Name = "WtkSettingsBox";
            this.WtkSettingsBox.TabStop = false;
            // 
            // browseButton
            // 
            resources.ApplyResources(this.browseButton, "browseButton");
            this.browseButton.Name = "browseButton";
            this.browseButton.UseVisualStyleBackColor = true;
            this.browseButton.Click += new System.EventHandler(this.browseButton_Click);
            // 
            // wtkPathLabel
            // 
            resources.ApplyResources(this.wtkPathLabel, "wtkPathLabel");
            this.wtkPathLabel.Name = "wtkPathLabel";
            // 
            // okayButton
            // 
            resources.ApplyResources(this.okayButton, "okayButton");
            this.okayButton.Name = "okayButton";
            this.okayButton.UseVisualStyleBackColor = true;
            this.okayButton.Click += new System.EventHandler(this.okayButton_Click);
            // 
            // cancelButton
            // 
            this.cancelButton.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            resources.ApplyResources(this.cancelButton, "cancelButton");
            this.cancelButton.Name = "cancelButton";
            this.cancelButton.UseVisualStyleBackColor = true;
            // 
            // wtkBrowser
            // 
            resources.ApplyResources(this.wtkBrowser, "wtkBrowser");
            this.wtkBrowser.ShowNewFolderButton = false;
            // 
            // Options
            // 
            this.AcceptButton = this.okayButton;
            resources.ApplyResources(this, "$this");
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = this.cancelButton;
            this.Controls.Add(this.cancelButton);
            this.Controls.Add(this.okayButton);
            this.Controls.Add(this.WtkSettingsBox);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "Options";
            this.ShowIcon = false;
            this.ShowInTaskbar = false;
            this.Load += new System.EventHandler(this.Options_Load);
            this.WtkSettingsBox.ResumeLayout(false);
            this.WtkSettingsBox.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TextBox wtkPathEdit;
        private System.Windows.Forms.GroupBox WtkSettingsBox;
        private System.Windows.Forms.Label wtkPathLabel;
        private System.Windows.Forms.Button browseButton;
        private System.Windows.Forms.Button okayButton;
        private System.Windows.Forms.Button cancelButton;
        private System.Windows.Forms.FolderBrowserDialog wtkBrowser;
    }
}