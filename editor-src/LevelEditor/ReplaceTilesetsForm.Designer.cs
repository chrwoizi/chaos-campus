namespace Javy
{
    partial class ReplaceTilesetsForm
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
            this.MapComboBox = new System.Windows.Forms.ComboBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.ReplaceComboBox = new System.Windows.Forms.ComboBox();
            this.label3 = new System.Windows.Forms.Label();
            this.ReplaceButton = new System.Windows.Forms.Button();
            this.button2 = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // MapComboBox
            // 
            this.MapComboBox.FormattingEnabled = true;
            this.MapComboBox.Location = new System.Drawing.Point(12, 35);
            this.MapComboBox.Name = "MapComboBox";
            this.MapComboBox.Size = new System.Drawing.Size(121, 21);
            this.MapComboBox.TabIndex = 0;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(9, 19);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(90, 13);
            this.label1.TabIndex = 1;
            this.label1.Text = "Tileset ID in Map:";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(205, 19);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(120, 13);
            this.label2.TabIndex = 2;
            this.label2.Text = "Replace with Tileset ID:";
            // 
            // ReplaceComboBox
            // 
            this.ReplaceComboBox.FormattingEnabled = true;
            this.ReplaceComboBox.Location = new System.Drawing.Point(208, 35);
            this.ReplaceComboBox.Name = "ReplaceComboBox";
            this.ReplaceComboBox.Size = new System.Drawing.Size(121, 21);
            this.ReplaceComboBox.TabIndex = 3;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(157, 38);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(22, 13);
            this.label3.TabIndex = 4;
            this.label3.Text = "--->";
            // 
            // ReplaceButton
            // 
            this.ReplaceButton.Location = new System.Drawing.Point(79, 74);
            this.ReplaceButton.Name = "ReplaceButton";
            this.ReplaceButton.Size = new System.Drawing.Size(75, 23);
            this.ReplaceButton.TabIndex = 5;
            this.ReplaceButton.Text = "Replace ID";
            this.ReplaceButton.UseVisualStyleBackColor = true;
            this.ReplaceButton.Click += new System.EventHandler(this.ReplaceButton_Click);
            // 
            // button2
            // 
            this.button2.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.button2.Location = new System.Drawing.Point(160, 74);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(75, 23);
            this.button2.TabIndex = 6;
            this.button2.Text = "Cancel";
            this.button2.UseVisualStyleBackColor = true;
            // 
            // ReplaceTilesetsForm
            // 
            this.AcceptButton = this.ReplaceButton;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = this.button2;
            this.ClientSize = new System.Drawing.Size(339, 109);
            this.Controls.Add(this.button2);
            this.Controls.Add(this.ReplaceButton);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.ReplaceComboBox);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.MapComboBox);
            this.Name = "ReplaceTilesetsForm";
            this.Text = "Tileset Replacer";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ComboBox MapComboBox;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.ComboBox ReplaceComboBox;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Button ReplaceButton;
        private System.Windows.Forms.Button button2;

    }
}