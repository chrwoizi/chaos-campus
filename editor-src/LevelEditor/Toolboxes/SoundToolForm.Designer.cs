namespace Javy
{
    partial class SoundToolForm
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
            this.soundControl1 = new Javy.Controls.SoundControl();
            this.SuspendLayout();
            // 
            // soundControl1
            // 
            this.soundControl1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.soundControl1.Location = new System.Drawing.Point(0, 0);
            this.soundControl1.MinimumSize = new System.Drawing.Size(248, 166);
            this.soundControl1.Name = "soundControl1";
            this.soundControl1.Size = new System.Drawing.Size(288, 268);
            this.soundControl1.TabIndex = 0;
            // 
            // SoundToolForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(288, 268);
            this.Controls.Add(this.soundControl1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.SizableToolWindow;
            this.Name = "SoundToolForm";
            this.TabText = "Sound Manager";
            this.Text = "Sound Manager";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.SoundToolForm_FormClosing);
            this.ResumeLayout(false);

        }

        #endregion

        private Javy.Controls.SoundControl soundControl1;











    }
}