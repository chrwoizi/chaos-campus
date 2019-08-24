using Javy.Controls;

namespace Javy
{
    partial class UndoToolForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.undoControl = new Javy.Controls.UndoControl();
            this.SuspendLayout();
            // 
            // undoControl
            // 
            this.undoControl.Dock = System.Windows.Forms.DockStyle.Fill;
            this.undoControl.Location = new System.Drawing.Point(0, 0);
            this.undoControl.Name = "undoControl";
            this.undoControl.Size = new System.Drawing.Size(292, 273);
            this.undoControl.TabIndex = 0;
            // 
            // UndoToolForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(292, 273);
            this.Controls.Add(this.undoControl);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.SizableToolWindow;
            this.Name = "UndoToolForm";
            this.TabText = "UndoToolForm";
            this.Text = "UndoToolForm";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.UndoToolForm_FormClosing);
            this.ResumeLayout(false);

        }

        #endregion

        private Javy.Controls.UndoControl undoControl;
    }
}